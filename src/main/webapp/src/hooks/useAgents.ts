import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { apiClient } from '@/lib/axios';

// Types for agents
export interface Agent {
  id: string;
  name: string;
  hostname: string;
  ipAddress: string;
  status: 'online' | 'offline' | 'maintenance';
  lastSeen: string;
  version: string;
  os: string;
  userId: string;
  userLogin: string;
  createdDate: string;
  lastModifiedDate: string;
}

export interface CreateAgentRequest {
  name: string;
  hostname: string;
  ipAddress?: string;
}

export interface UpdateAgentRequest {
  id: string;
  name?: string;
  status?: 'online' | 'offline' | 'maintenance';
}

// Query keys
export const agentKeys = {
  all: ['agents'] as const,
  lists: () => [...agentKeys.all, 'list'] as const,
  list: (filters: Record<string, any>) => [...agentKeys.lists(), filters] as const,
  details: () => [...agentKeys.all, 'detail'] as const,
  detail: (id: string) => [...agentKeys.details(), id] as const,
};

// API functions
const fetchAgents = async (params: { page?: number; size?: number; search?: string } = {}): Promise<{
  content: Agent[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}> => {
  const response = await apiClient.get('/agents', { params });
  return response.data;
};

const fetchAgent = async (id: string): Promise<Agent> => {
  const response = await apiClient.get(`/agents/${id}`);
  return response.data;
};

const createAgent = async (data: CreateAgentRequest): Promise<Agent> => {
  const response = await apiClient.post('/agents', data);
  return response.data;
};

const updateAgent = async (data: UpdateAgentRequest): Promise<Agent> => {
  const response = await apiClient.put(`/agents/${data.id}`, data);
  return response.data;
};

const deleteAgent = async (id: string): Promise<void> => {
  await apiClient.delete(`/agents/${id}`);
};

// Hooks
export const useAgents = (params: { page?: number; size?: number; search?: string } = {}) => {
  return useQuery({
    queryKey: agentKeys.list(params),
    queryFn: () => fetchAgents(params),
    staleTime: 1000 * 30, // 30 seconds
  });
};

export const useAgent = (id: string) => {
  return useQuery({
    queryKey: agentKeys.detail(id),
    queryFn: () => fetchAgent(id),
    enabled: !!id,
    staleTime: 1000 * 30, // 30 seconds
  });
};

export const useCreateAgent = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: createAgent,
    onSuccess: () => {
      // Invalidate and refetch agents list
      queryClient.invalidateQueries({ queryKey: agentKeys.lists() });
    },
  });
};

export const useUpdateAgent = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: updateAgent,
    onSuccess: (updatedAgent) => {
      // Update the specific agent in cache
      queryClient.setQueryData(agentKeys.detail(updatedAgent.id), updatedAgent);
      // Invalidate agents lists
      queryClient.invalidateQueries({ queryKey: agentKeys.lists() });
    },
  });
};

export const useDeleteAgent = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: deleteAgent,
    onSuccess: (_, deletedId) => {
      // Remove from cache
      queryClient.removeQueries({ queryKey: agentKeys.detail(deletedId) });
      // Invalidate agents lists
      queryClient.invalidateQueries({ queryKey: agentKeys.lists() });
    },
  });
};