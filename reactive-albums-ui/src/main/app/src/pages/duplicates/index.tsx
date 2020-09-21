import React, { FC, useEffect, useState } from 'react';
import { DuplicateMatch } from '@types';
import { useDuplicateMatches, useTotalDuplicateCount } from '@reducers';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import { MatchList } from './match-list';
import { MatchView } from './match-view';
import Container from 'react-bootstrap/Container';
import { useTranslation } from 'react-i18next';
import { Link, useLocation } from 'react-router-dom';
import NavLink from 'react-bootstrap/NavLink';
import { Conditional } from '@components';

export const DuplicatesNavItem: FC = () => {
  const { t } = useTranslation();
  const loc = useLocation();
  const totalDuplicates = useTotalDuplicateCount();

  return (
    <NavLink as={Link} to="/duplicates" active={loc.pathname === '/duplicates'} disabled={totalDuplicates === 0}>
      {t('duplicates.nav-item.label', { totalDuplicates })}
    </NavLink>
  );
};

const DuplicatesPage: FC = () => {
  const { t } = useTranslation();
  const [selectedMatch, setSelectedMatch] = useState<DuplicateMatch | undefined>();
  const matches = useDuplicateMatches();

  useEffect(() => {
    if (!!selectedMatch) {
      if (!matches || !matches.some((dm) => dm.id === selectedMatch.id)) {
        setSelectedMatch(matches[0] || undefined);
      }
    }
  }, [matches, selectedMatch, setSelectedMatch]);

  return (
    <Container fluid className="pb-4">
      <Conditional
        condition={matches.isNotEmpty()}
        orElse={<h2 className="text-center mt-5">{t('duplicates.no_duplicate_matches_available')}</h2>}
      >
        <Row>
          <Col sm={12} md={3}>
            <MatchList matches={matches} onMatchSelect={setSelectedMatch} selectedMatch={selectedMatch} />
          </Col>
          <Col sm={12} md={9}>
            {!!selectedMatch ? <MatchView match={selectedMatch} /> : t('duplicates.no_source_selected')}
          </Col>
        </Row>
      </Conditional>
    </Container>
  );
};

export default DuplicatesPage;
