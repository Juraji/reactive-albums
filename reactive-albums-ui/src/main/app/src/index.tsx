import React from 'react';
import ReactDOM from 'react-dom';
import 'url-search-params-polyfill';
import { App } from './App';
import * as serviceWorker from './service-worker';
import './i18n';

import './styles/index.scss';
import { isDevelopmentEnv } from '@utils';

ReactDOM.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
  document.getElementById('root')
);

if (isDevelopmentEnv()) {
  serviceWorker.unregister();
}
