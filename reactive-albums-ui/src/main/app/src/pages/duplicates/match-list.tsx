import React, { FC } from 'react';
import { DuplicateMatch } from '@types';
import ListGroup from 'react-bootstrap/ListGroup';

interface MatchListProps {
  matches: DuplicateMatch[];
  selectedMatch: DuplicateMatch | undefined;
  onMatchSelect: (match: DuplicateMatch) => void;
}

export const MatchList: FC<MatchListProps> = ({ matches, selectedMatch, onMatchSelect }) => (
  <ListGroup>
    {matches.map((dm, idx) => (
      <ListGroup.Item
        key={idx}
        action
        onClick={() => onMatchSelect(dm)}
        active={dm.id === selectedMatch?.id}
        className="d-flex flex-row"
      >
        <span className="flex-grow-1 text-ellipsis text-center">{dm.picture?.displayName}</span>
        <span className="mx-2 font-weight-bold">&lt;{dm.similarity}%&gt;</span>
        <span className="flex-grow-1 text-ellipsis text-center">{dm.target?.displayName}</span>
      </ListGroup.Item>
    ))}
  </ListGroup>
);
