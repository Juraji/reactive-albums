import React, { FC } from 'react';
import { useParams } from 'react-router-dom';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import { Conditional } from '@components';
import { PictureTags } from './picture-tags';
import { PictureView } from './picture-view';
import Container from 'react-bootstrap/Container';
import { PictureDetails } from './picture-details';
import { PictureActionBar } from './picture-action-bar';
import { PictureDuplicateList } from './picture-duplicate-list';
import { usePicture } from '@reducers';

const PicturePage: FC = () => {
  const { pictureId } = useParams();
  const picture = usePicture(pictureId);

  return (
    <Container fluid>
      <Conditional condition={!!picture}>
        <Row>
          <Col sm={12} md={9}>
            <PictureView pictureId={pictureId} />
          </Col>
          <Col sm={12} md={3}>
            <PictureActionBar picture={picture!} />
            <PictureDetails picture={picture!} />
            <PictureTags picture={picture!} />
            <PictureDuplicateList pictureId={pictureId} />
          </Col>
        </Row>
      </Conditional>
    </Container>
  );
};

export default PicturePage;
