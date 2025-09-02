import React from 'react';
import { Card, CardHeader, CardTitle, CardContent, CardDescription } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/custom/badge';

// Mock data - in a real app, this would come from an API
const agentStats = {
  total: 125,
  online: 112,
  offline: 8,
  maintenance: 5,
  critical: 3
};

const backupStats = {
  total: 87,
  success: 82,
  warning: 3,
  failed: 2,
  pending: 0
};

const recentActivities = [
  { id: 1, type: 'Backup', description: 'Daily Exchange Backup completed successfully', timestamp: '2025-06-18T14:30:00Z', status: 'success' },
  { id: 2, type: 'Agent', description: 'Server-003 went offline', timestamp: '2025-06-18T13:45:00Z', status: 'error' },
  { id: 3, type: 'Deployment', description: 'Application update deployed to 15 agents', timestamp: '2025-06-18T12:20:00Z', status: 'success' },
  { id: 4, type: 'Script', description: 'Maintenance script executed on Server-005', timestamp: '2025-06-18T11:10:00Z', status: 'success' },
  { id: 5, type: 'Remote', description: 'Remote session with Server-002 ended', timestamp: '2025-06-18T10:05:00Z', status: 'info' },
  { id: 5, type: 'Backup', description: 'Difference Backup completed', timestamp: '2025-06-18T10:05:00Z', status: 'warning' },];

const Dashboard: React.FC = () => {
  // Format date to a more readable format
  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return new Intl.DateTimeFormat('fr-FR', {
      dateStyle: 'medium',
      timeStyle: 'short'
    }).format(date);
  };

  // Get status badge class based on status
  const getStatusBadgeVariant= (status: string) => {
    switch (status.toLowerCase()) {
      case 'success':
        return 'success';
      case 'warning':
        return 'warning';
      case 'error':
        return 'destructive';
      case 'info':
        return 'info';
      default:
        return 'default';
    }
  };

  return (

      <div className="space-y-6">
        <div className="flex justify-between items-center">
          <h1 className="text-3xl font-bold tracking-tight">Dashboard</h1>
          <div className="flex space-x-2">
            <Button variant="outline">Generate Report</Button>
            <Button>Refresh Data</Button>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium">Total Agents</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{agentStats.total}</div>
              <p className="text-xs text-muted-foreground mt-1">
                {agentStats.online} online, {agentStats.offline} offline
              </p>
            </CardContent>
          </Card>
          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium">Agents in Maintenance</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{agentStats.maintenance}</div>
              <p className="text-xs text-muted-foreground mt-1">
                {agentStats.critical} with critical issues
              </p>
            </CardContent>
          </Card>
          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium">Backup Jobs</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{backupStats.total}</div>
              <p className="text-xs text-muted-foreground mt-1">
                {backupStats.success} successful, {backupStats.failed} failed
              </p>
            </CardContent>
          </Card>
          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-medium">Pending Tasks</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{backupStats.pending}</div>
              <p className="text-xs text-muted-foreground mt-1">
                No pending tasks at the moment
              </p>
            </CardContent>
          </Card>
        </div>

        <Card>
          <CardHeader>
            <CardTitle>Recent Activities</CardTitle>
            <CardDescription>Latest events from your agents and services</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {recentActivities.map((activity) => (
                <div key={activity.id} className="flex items-start space-x-4">
                  <div className="min-w-[4rem] text-sm font-medium text-muted-foreground">
                    {activity.type}
                  </div>
                  <div className="flex-1">
                    <p className="text-sm font-medium leading-none">{activity.description}</p>
                    <p className="text-sm text-muted-foreground">{formatDate(activity.timestamp)}</p>
                  </div>
                  <div>
                    <Badge outline variant={getStatusBadgeVariant(activity.status)} >
                      {activity.status}
                    </Badge>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <Card>
            <CardHeader>
              <CardTitle>Agent Status Distribution</CardTitle>
              <CardDescription>Overview of your agent fleet status</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="h-[200px] flex items-center justify-center">
                <p className="text-muted-foreground">Chart visualization would be here</p>
              </div>
            </CardContent>
          </Card>
          <Card>
            <CardHeader>
              <CardTitle>Backup Job Performance</CardTitle>
              <CardDescription>Success rate over the last 30 days</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="h-[200px] flex items-center justify-center">
                <p className="text-muted-foreground">Chart visualization would be here</p>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>

  );
};

export default Dashboard;
