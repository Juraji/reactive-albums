import React, { FC, lazy, Suspense } from 'react';
import { Provider } from 'react-redux';
import { appStore } from '@reducers';
import { BrowserRouter, Route, Switch } from 'react-router-dom';
import { TOAST_TIMEOUT } from './config.json';
import { BootstrapToastAdapter, DefaultRoute, GlobalEffectors, NavigationBar } from '@components';
import Spinner from 'react-bootstrap/Spinner';
import { ToastProvider } from 'react-toast-notifications';
import { HomeNavItem } from './pages/home/home-nav-item';
import { DirectoriesNavItem } from './pages/directories/directories-nav-item';

const HomePage = lazy(() => import('./pages/home'));
const DirectoriesPage = lazy(() => import('./pages/directories'));
const PicturePage = lazy(() => import('./pages/picture'));

const AppView: FC = () => (
  <BrowserRouter>
    <NavigationBar>
      <HomeNavItem />
      <DirectoriesNavItem />
    </NavigationBar>
    <ToastProvider autoDismiss={true} autoDismissTimeout={TOAST_TIMEOUT} components={{ Toast: BootstrapToastAdapter }}>
      <GlobalEffectors />
      <Suspense fallback={<Spinner animation="border" />}>
        <Switch>
          <Route exact path="/home" component={HomePage} />
          <Route exact path="/directories" component={DirectoriesPage} />
          <Route exact path="/picture/:pictureId" component={PicturePage} />
          <DefaultRoute to="/home" />
        </Switch>
      </Suspense>
    </ToastProvider>
  </BrowserRouter>
);

export const App: FC = () => (
  <Provider store={appStore}>
    <AppView />
  </Provider>
);
