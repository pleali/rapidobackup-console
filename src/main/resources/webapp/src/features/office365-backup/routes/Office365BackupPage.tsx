import React, { useState } from 'react';
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/card';
import { Table, TableHeader, TableBody, TableRow, TableHead, TableCell } from '@/components/ui/table';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';

// Mock data - in a real app, this would come from an API
const mockBackupConfigs = [
  { id: 1, name: 'Daily Exchange Backup', type: 'Exchange', schedule: 'Daily at 22:00', lastRun: '2025-06-17T22:00:00Z', status: 'Success', retention: '30 days' },
  { id: 2, name: 'Weekly SharePoint Backup', type: 'SharePoint', schedule: 'Weekly on Sunday at 01:00', lastRun: '2025-06-16T01:00:00Z', status: 'Success', retention: '90 days' },
  { id: 3, name: 'Daily OneDrive Backup', type: 'OneDrive', schedule: 'Daily at 23:00', lastRun: '2025-06-17T23:00:00Z', status: 'Warning', retention: '30 days' },
  { id: 4, name: 'Monthly Teams Backup', type: 'Teams', schedule: 'Monthly on 1st at 02:00', lastRun: '2025-06-01T02:00:00Z', status: 'Success', retention: '365 days' },
  { id: 5, name: 'Hourly Exchange Critical', type: 'Exchange', schedule: 'Hourly', lastRun: '2025-06-18T14:00:00Z', status: 'Failed', retention: '7 days' },
];

const Office365Backup: React.FC = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [backupConfigs, setBackupConfigs] = useState(mockBackupConfigs);

  // Filter backup configs based on search term
  const filteredConfigs = backupConfigs.filter(config => 
    config.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    config.type.toLowerCase().includes(searchTerm.toLowerCase()) ||
    config.status.toLowerCase().includes(searchTerm.toLowerCase())
  );

  // Format date to a more readable format
  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return new Intl.DateTimeFormat('fr-FR', {
      dateStyle: 'medium',
      timeStyle: 'short'
    }).format(date);
  };

  // Get status badge class based on status
  const getStatusBadgeClass = (status: string) => {
    switch (status.toLowerCase()) {
      case 'success':
        return 'bg-green-100 text-green-800';
      case 'warning':
        return 'bg-yellow-100 text-yellow-800';
      case 'failed':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold tracking-tight">Office 365 Backup</h1>
        <Button>Create New Backup Configuration</Button>
      </div>

      <div className="flex items-center py-4">
        <Input
          placeholder="Search backup configurations..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="max-w-sm"
        />
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Backup Configurations</CardTitle>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Name</TableHead>
                <TableHead>Type</TableHead>
                <TableHead>Schedule</TableHead>
                <TableHead>Last Run</TableHead>
                <TableHead>Status</TableHead>
                <TableHead>Retention</TableHead>
                <TableHead>Actions</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {filteredConfigs.length > 0 ? (
                filteredConfigs.map((config) => (
                  <TableRow key={config.id}>
                    <TableCell className="font-medium">{config.name}</TableCell>
                    <TableCell>{config.type}</TableCell>
                    <TableCell>{config.schedule}</TableCell>
                    <TableCell>{formatDate(config.lastRun)}</TableCell>
                    <TableCell>
                      <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusBadgeClass(config.status)}`}>
                        {config.status}
                      </span>
                    </TableCell>
                    <TableCell>{config.retention}</TableCell>
                    <TableCell>
                      <div className="flex space-x-2">
                        <Button variant="outline" size="sm">Edit</Button>
                        <Button variant="outline" size="sm">Run Now</Button>
                        <Button variant="outline" size="sm">History</Button>
                      </div>
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell colSpan={7} className="text-center py-6">
                    No backup configurations found matching your search criteria.
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

export default Office365Backup;
