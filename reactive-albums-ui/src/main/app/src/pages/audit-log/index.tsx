import React, { FC } from 'react';
import Container from 'react-bootstrap/Container';
import { AuditLogControls } from './audit-log-controls';
import { useAuditLogEntries } from '@reducers';

interface AuditLogPageProps {}

const AuditLogPage: FC<AuditLogPageProps> = () => {
  const auditLogEntries = useAuditLogEntries();

  return (
    <Container fluid>
      <pre>{JSON.stringify(auditLogEntries, null, 2)}</pre>

      <AuditLogControls />
    </Container>
  );
};

export default AuditLogPage;
