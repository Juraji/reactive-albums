import React, { FC } from 'react';
import Container from 'react-bootstrap/Container';
import { AuditLogControls } from './audit-log-controls';
import { AuditLogEntryTable } from './audit-log-entry-table';

interface AuditLogPageProps {}

const AuditLogPage: FC<AuditLogPageProps> = () => (
  <Container fluid>
    <AuditLogEntryTable />
    <AuditLogControls />
  </Container>
);

export default AuditLogPage;
