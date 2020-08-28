import i18next from 'i18next';
import { initReactI18next } from 'react-i18next';
import { isDevelopmentEnv } from '@utils';
import translation from './translation.json';

i18next.use(initReactI18next).init({
  lng: 'nl',
  debug: isDevelopmentEnv(),
  resources: { nl: { translation } },
});
