import React, { useState } from 'react';
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/card';
import { Table, TableHeader, TableBody, TableRow, TableHead, TableCell } from '@/components/ui/table';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { 
  Badge, 
  BadgeSuccess, 
  BadgeError, 
  BadgeWarning, 
  BadgeInfo,
  BadgeWithIcon,
  CounterBadge,
  useAnimatedBadge 
} from "@/components/custom/badge"
import { CheckIcon, XIcon, AlertTriangleIcon, InfoIcon } from "lucide-react"

export function BadgeExamples() {
  const [notifications, setNotifications] = React.useState(3)
  const animateClass = useAnimatedBadge(notifications > 0)

  return (
 <div className="p-8 space-y-8">
      {/* Variants de base */}
      <section>
        <h3 className="text-lg font-semibold mb-4">Variants de base</h3>
        <div className="flex flex-wrap gap-2">
          <Badge>Default</Badge>
          <Badge variant="secondary">Secondary</Badge>
          <Badge variant="destructive">Destructive</Badge>
          <Badge variant="outline">Outline</Badge>
          <Badge variant="success">Success</Badge>
          <Badge variant="warning">Warning</Badge>
          <Badge variant="info">Info</Badge>
          <Badge variant="ghost">Ghost</Badge>
        </div>
      </section>

      {/* Tailles */}
      <section>
        <h3 className="text-lg font-semibold mb-4">Tailles</h3>
        <div className="flex items-center gap-2">
          <Badge size="sm">Small</Badge>
          <Badge>Default</Badge>
          <Badge size="lg">Large</Badge>
          <Badge size="xl">Extra Large</Badge>
        </div>
      </section>

      {/* Formes */}
      <section>
        <h3 className="text-lg font-semibold mb-4">Formes</h3>
        <div className="flex gap-2">
          <Badge shape="default">Pill</Badge>
          <Badge shape="square">Square</Badge>
          <Badge shape="none">None</Badge>
        </div>
      </section>

      {/* Badges spécialisés */}
      <section>
        <h3 className="text-lg font-semibold mb-4">Badges spécialisés</h3>
        <div className="flex flex-wrap gap-2">
          <BadgeSuccess>Succès</BadgeSuccess>
          <BadgeError>Erreur</BadgeError>
          <BadgeWarning>Attention</BadgeWarning>
          <BadgeInfo>Information</BadgeInfo>
        </div>
      </section>

      {/* Avec point indicateur */}
      <section>
        <h3 className="text-lg font-semibold mb-4">Avec indicateur</h3>
        <div className="flex gap-2">
          <Badge dot variant="success">En ligne</Badge>
          <Badge dot variant="destructive">Hors ligne</Badge>
          <Badge dot variant="warning">En attente</Badge>
        </div>
      </section>

      {/* Avec icônes */}
      <section>
        <h3 className="text-lg font-semibold mb-4">Avec icônes</h3>
        <div className="flex flex-wrap gap-2 items-center">
          <BadgeWithIcon 
            icon={<CheckIcon />} 
            variant="success"
          >
            Validé
          </BadgeWithIcon>
          <BadgeWithIcon 
            icon={<XIcon />} 
            variant="destructive"
            iconPosition="right"
          >
            Rejeté
          </BadgeWithIcon>
          <BadgeWithIcon 
            icon={<AlertTriangleIcon />} 
            variant="warning"
          >
            Attention
          </BadgeWithIcon>
          <BadgeWithIcon 
            icon={<InfoIcon />} 
            variant="info"
            size="lg"
          >
            Info grande
          </BadgeWithIcon>
          <BadgeWithIcon 
            icon={<CheckIcon />} 
            variant="success"
            outline
          >
            Outline avec icône
          </BadgeWithIcon>
        </div>
      </section>

      {/* Badges compteurs */}
      <section>
        <h3 className="text-lg font-semibold mb-4">Compteurs</h3>
        <div className="flex gap-4 items-center">
          <div className="relative">
            <button className="p-2 bg-gray-100 rounded">
              Messages
            </button>
            <CounterBadge 
              count={5} 
              variant="destructive"
              size="sm"
              className="absolute -top-2 -right-2"
            />
          </div>
          
          <div className="relative">
            <button className="p-2 bg-gray-100 rounded">
              Notifications
            </button>
            <CounterBadge 
              count={notifications} 
              variant="info"
              size="sm"
              className={`absolute -top-2 -right-2 ${animateClass}`}
            />
          </div>

          <div className="relative">
            <button className="p-2 bg-gray-100 rounded">
              Panier
            </button>
            <CounterBadge 
              count={150} 
              max={99}
              variant="success"
              size="sm"
              className="absolute -top-2 -right-2"
            />
          </div>
        </div>
      </section>

      {/* Outline variants */}
      <section>
        <h3 className="text-lg font-semibold mb-4">Variants outline</h3>
        <div className="flex flex-wrap gap-2">
          <Badge outline>Default outline</Badge>
          <Badge outline variant="success">Success outline</Badge>
          <Badge outline variant="destructive">Error outline</Badge>
          <Badge outline variant="warning">Warning outline</Badge>
          <Badge outline variant="info">Info outline</Badge>
          <Badge outline variant="secondary">Secondary outline</Badge>
        </div>
      </section>

      {/* Responsive */}
      <section>
        <h3 className="text-lg font-semibold mb-4">Responsive</h3>
        <Badge responsive variant="info">
          Ce badge s'adapte à la taille d'écran
        </Badge>
      </section>

      {/* Actions */}
      <section>
        <h3 className="text-lg font-semibold mb-4">Test animation</h3>
        <button 
          onClick={() => setNotifications(prev => prev + 1)}
          className="px-4 py-2 bg-blue-500 text-white rounded"
        >
          Ajouter notification
        </button>
      </section>
    </div>
  )
}

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
      <BadgeExamples />
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
