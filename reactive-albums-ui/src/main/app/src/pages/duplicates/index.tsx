import React, { FC, useMemo, useState } from 'react';
import Container from 'react-bootstrap/Container';
import Col from 'react-bootstrap/Col';
import Row from 'react-bootstrap/Row';
import { useGroupedDuplicateMatches } from '@reducers';
import { SourcePicturesSelect } from './source-pictures-select';
import { MatchGroup } from './match-group';
import { Conditional } from '@components';
import { useTranslation } from 'react-i18next';

const DuplicatesPage: FC = () => {
  const { t } = useTranslation();
  const { sourcePictureIds, matchGroups } = useGroupedDuplicateMatches();
  const [activeMatchGroupId, setActiveMatchGroupId] = useState(sourcePictureIds[0]);
  const activeMatchGroup = useMemo(() => matchGroups[activeMatchGroupId], [matchGroups, activeMatchGroupId]);

  return (
    <Container fluid className="pb-4">
      <Row>
        <Col sm={12} md={3}>
          <SourcePicturesSelect
            sourcePictureIds={sourcePictureIds}
            activeId={activeMatchGroupId}
            onActivateId={setActiveMatchGroupId}
          />
        </Col>
        <Col sm={12} md={9}>
          <Conditional
            condition={activeMatchGroupId !== undefined && !!activeMatchGroup}
            orElse={<p>{t('duplicates.no_source_selected')}</p>}
          >
            <MatchGroup sourcePictureId={activeMatchGroupId} matches={activeMatchGroup} />
          </Conditional>
        </Col>
      </Row>
    </Container>
  );
};

export default DuplicatesPage;
