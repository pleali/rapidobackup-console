import React, { useState } from 'react';
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/card';
import { Table, TableHeader, TableBody, TableRow, TableHead, TableCell } from '@/components/ui/table';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from "@/components/custom/badge"


// Mock data - in a real app, this would come from an API
const mockAgents = [
  { id: 1, name: 'Server-001', hostname: 'server001.example.com', ip: '192.168.1.101', status: 'Online', lastSeen: '2025-06-18T14:45:00Z', os: 'Windows Server 2022' },
  { id: 2, name: 'Server-002', hostname: 'server002.example.com', ip: '192.168.1.102', status: 'Online', lastSeen: '2025-06-18T14:50:00Z', os: 'Ubuntu 24.04 LTS' },
  { id: 3, name: 'Server-003', hostname: 'server003.example.com', ip: '192.168.1.103', status: 'Offline', lastSeen: '2025-06-17T09:30:00Z', os: 'Windows Server 2022' },
  { id: 4, name: 'Server-004', hostname: 'server004.example.com', ip: '192.168.1.104', status: 'Online', lastSeen: '2025-06-18T14:55:00Z', os: 'CentOS 9' },
  { id: 5, name: 'Server-005', hostname: 'server005.example.com', ip: '192.168.1.105', status: 'Maintenance', lastSeen: '2025-06-18T12:20:00Z', os: 'Windows Server 2022' },
];

const Agents: React.FC = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [agents, setAgents] = useState(mockAgents);

  // Filter agents based on search term
  const filteredAgents = agents.filter(agent => 
    agent.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    agent.hostname.toLowerCase().includes(searchTerm.toLowerCase()) ||
    agent.ip.toLowerCase().includes(searchTerm.toLowerCase()) ||
    agent.os.toLowerCase().includes(searchTerm.toLowerCase())
  );

  // Format date to a more readable format
  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return new Intl.DateTimeFormat('fr-FR', {
      dateStyle: 'medium',
      timeStyle: 'short'
    }).format(date);
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold tracking-tight">Agents</h1>
        <Button>Add New Agent</Button>
      </div>
      <div className="flex items-center py-4">
        <Input
          placeholder="Search agents..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="max-w-sm"
        />
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Agent List</CardTitle>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Name</TableHead>
                <TableHead>Hostname</TableHead>
                <TableHead>IP Address</TableHead>
                <TableHead>Status</TableHead>
                <TableHead>Last Seen</TableHead>
                <TableHead>OS</TableHead>
                <TableHead>Actions</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {filteredAgents.length > 0 ? (
                filteredAgents.map((agent) => (
                  <TableRow key={agent.id}>
                    <TableCell className="font-medium">{agent.name}</TableCell>
                    <TableCell>{agent.hostname}</TableCell>
                    <TableCell>{agent.ip}</TableCell>
                    <TableCell>
                      <Badge outline={true} variant={
                        agent.status === 'Online' ? 'success' :
                        agent.status === 'Offline' ? 'destructive' :
                        'warning'
                      }>
                        {agent.status}
                      </Badge>
                    </TableCell>
                    <TableCell>{formatDate(agent.lastSeen)}</TableCell>
                    <TableCell>{agent.os}</TableCell>
                    <TableCell>
                      <div className="flex space-x-2">
                        <Button variant="outline" size="sm">Details</Button>
                        <Button variant="outline" size="sm">Remote</Button>
                        <Button variant="outline" size="sm">Backup</Button>
                      </div>
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell colSpan={7} className="text-center py-6">
                    No agents found matching your search criteria.
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </CardContent>
      </Card>
    </div>

  );
};

export default Agents;
