import React from 'react';
import ReactDOM from 'react-dom';
import 'url-search-params-polyfill';
import { App } from './App';
import * as serviceWorker from './service-worker';
import './i18n';
import { isDevelopmentEnv } from '@utils';
import installExtensions from './@utils/prototypes';

import './styles/index.scss';

installExtensions();

ReactDOM.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
  document.getElementById('root')
);

if (isDevelopmentEnv()) {
  serviceWorker.unregister();
}
