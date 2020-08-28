import React, { FC } from 'react';
import { useTranslation } from 'react-i18next';
import Navbar from 'react-bootstrap/Navbar';
import { Link } from 'react-router-dom';
import Nav from 'react-bootstrap/Nav';

export const NavigationBar: FC = ({ children }) => {
  const { t } = useTranslation();

  return (
    <Navbar bg="primary" variant="dark" expand="lg" sticky="top" className="mb-2">
      <Navbar.Brand as={Link} to="/">
        {t('navigation.title')}
      </Navbar.Brand>
      <Navbar.Toggle aria-controls="main-nav" />
      <Navbar.Collapse id="main-nav">
        <Nav className="mr-auto">{children}</Nav>
      </Navbar.Collapse>
    </Navbar>
  );
};
