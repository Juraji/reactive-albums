import React, { FC, lazy, Suspense } from 'react';
import { Provider } from 'react-redux';
import { appStore } from '@reducers';
import { BrowserRouter, Route, Switch } from 'react-router-dom';
import { TOAST_TIMEOUT } from './config.json';
import { BootstrapToastAdapter, DefaultRoute, GlobalEffectors, NavigationBar } from '@components';
import Spinner from 'react-bootstrap/Spinner';
import { ToastProvider } from 'react-toast-notifications';
import { HomeNavItem } from './pages/home/home-nav-item';

const HomePage = lazy(() => import('./pages/home'));

const AppView: FC = () => (
  <BrowserRouter>
    <NavigationBar>
      <HomeNavItem />
    </NavigationBar>
    <ToastProvider autoDismiss={true} autoDismissTimeout={TOAST_TIMEOUT} components={{ Toast: BootstrapToastAdapter }}>
      <GlobalEffectors />
      <Suspense fallback={<Spinner animation="border" />}>
        <Switch>
          <Route exact path="/home" component={HomePage} />
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
