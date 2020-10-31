import React, { FC, lazy, Suspense } from 'react';
import { Provider } from 'react-redux';
import { appStore } from '@reducers';
import { HashRouter, Route, Switch } from 'react-router-dom';
import { TOAST_TIMEOUT } from './config.json';
import { BootstrapToastAdapter, DefaultRoute, GlobalEffectors, NavigationBar } from '@components';
import Spinner from 'react-bootstrap/Spinner';
import { ToastProvider } from 'react-toast-notifications';
import { AuditLogNavItem, DirectoriesNavItem, DuplicatesNavItem, HomeNavItem, TagsNavItem } from './pages/@nav-items';

const HomePage = lazy(() => import('./pages/home'));
const DirectoriesPage = lazy(() => import('./pages/directories'));
const PicturePage = lazy(() => import('./pages/picture'));
const DuplicatesPage = lazy(() => import('./pages/duplicates'));
const TagManagementPage = lazy(() => import('./pages/tags'));
const AuditLogPage = lazy(() => import('./pages/audit-log'));

const AppView: FC = () => (
  <HashRouter>
    <NavigationBar>
      <HomeNavItem />
      <DuplicatesNavItem />
      <DirectoriesNavItem />
      <TagsNavItem />
      <AuditLogNavItem />
    </NavigationBar>
    <ToastProvider autoDismiss={true} autoDismissTimeout={TOAST_TIMEOUT} components={{ Toast: BootstrapToastAdapter }}>
      <GlobalEffectors />
      <Suspense fallback={<Spinner animation="border" />}>
        <Switch>
          <Route exact path="/home" component={HomePage} />
          <Route exact path="/directories" component={DirectoriesPage} />
          <Route exact path="/picture/:pictureId" component={PicturePage} />
          <Route exact path="/duplicates" component={DuplicatesPage} />
          <Route exact path="/tags" component={TagManagementPage} />
          <Route exact path="/audit-log" component={AuditLogPage} />
          <DefaultRoute to="/home" />
        </Switch>
      </Suspense>
    </ToastProvider>
  </HashRouter>
);

export const App: FC = () => (
  <Provider store={appStore}>
    <AppView />
  </Provider>
);
