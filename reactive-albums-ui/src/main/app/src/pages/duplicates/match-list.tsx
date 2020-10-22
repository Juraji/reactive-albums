import React, { FC, useMemo } from 'react';
import { DuplicateMatch } from '@types';
import Table from 'react-bootstrap/Table';

import './match-list.scss';

interface MatchListItemProps {
  match: DuplicateMatch;
  selectedMatch: DuplicateMatch | undefined;
  onMatchSelect: (match: DuplicateMatch) => void;
}

export const MatchListItem: FC<MatchListItemProps> = ({ match, selectedMatch, onMatchSelect }) => {
  const rowClassName = useMemo(() => {
    const classes = ['cursor-pointer'];

    if (match.id === selectedMatch?.id) {
      classes.push('active');
    }

    return classes.join(' ');
  }, [match, selectedMatch]);

  return (
    <tr className={rowClassName} onClick={() => onMatchSelect(match)}>
      <td>{match.picture?.displayName}</td>
      <td className="font-weight-bold">{match.similarity}%</td>
    </tr>
  );
};

interface MatchListProps {
  matches: DuplicateMatch[];
  selectedMatch: DuplicateMatch | undefined;
  onMatchSelect: (match: DuplicateMatch) => void;
}

export const MatchList: FC<MatchListProps> = ({ matches, ...rest }) => {
  return (
    <Table hover responsive size="sm" className="match-list-table">
      <tbody>
        {matches.map((dm, idx) => (
          <MatchListItem key={idx} match={dm} {...rest} />
        ))}
      </tbody>
    </Table>
  );
};
