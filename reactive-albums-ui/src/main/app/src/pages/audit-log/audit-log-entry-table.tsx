import React, { FC } from 'react';
import { fetchAuditLogPageWithAggregateId, useAuditLogEntries } from '@reducers';
import Table from 'react-bootstrap/Table';
import { AggregateType } from '@types';
import { Folder, HelpCircle, Image } from 'react-feather';
import { useDispatch } from '@hooks';
import { useTranslation } from 'react-i18next';
import Button from 'react-bootstrap/Button';
import { Link } from 'react-router-dom';

interface AggregateTypeIconProps {
  aggregateType: AggregateType;
  aggregateId: string;
}

const AggregateTypeIcon: FC<AggregateTypeIconProps> = ({ aggregateType, aggregateId }) => {
  switch (aggregateType) {
    case AggregateType.DIRECTORY:
      return <Folder />;
    case AggregateType.PICTURE:
      return (
        <Link to={`/picture/${aggregateId}`}>
          <Image />
        </Link>
      );
    default:
      return <HelpCircle />;
  }
};

interface EntryMessageProps {
  message: string;
}

const EntryMessage: FC<EntryMessageProps> = ({ message }) => {
  return <>{message}</>;
};

interface AuditLogEntryTableProps {}

export const AuditLogEntryTable: FC<AuditLogEntryTableProps> = () => {
  const dispatch = useDispatch();
  const { t } = useTranslation();
  const { content: entries, ...page } = useAuditLogEntries();

  function onAggregateIdClick(aggregateId: string) {
    return () =>
      dispatch(
        fetchAuditLogPageWithAggregateId({
          page: page.pageNumber,
          size: page.requestedPageSize,
          sort: page.sort,
          aggregateId,
        })
      );
  }

  return (
    <Table size="sm">
      <thead>
        <tr>
          <th scope="column">{t('audit_log.table.headers.timestamp')}</th>
          <th scope="column">{t('audit_log.table.headers.aggregate_type')}</th>
          <th scope="column">{t('audit_log.table.headers.aggregate_id')}</th>
          <th scope="column">{t('audit_log.table.headers.message')}</th>
        </tr>
      </thead>
      <tbody>
        {entries.map((entry, idx) => (
          <tr key={idx}>
            <td>{t('audit_log.table.timestamp', entry)}</td>
            <td>
              <AggregateTypeIcon aggregateType={entry.aggregateType} aggregateId={entry.aggregateId} />
            </td>
            <td>
              <Button variant="link" onClick={onAggregateIdClick(entry.aggregateId)}>
                {entry.aggregateId}
              </Button>
            </td>
            <td>
              <EntryMessage message={entry.message} />
            </td>
          </tr>
        ))}
      </tbody>
    </Table>
  );
};
