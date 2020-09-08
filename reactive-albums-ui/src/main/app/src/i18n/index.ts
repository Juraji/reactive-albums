import i18next from 'i18next';
import { initReactI18next } from 'react-i18next';
import { isDevelopmentEnv } from '@utils';
import translation from './translation.json';
import { format as formatDate, parseISO } from 'date-fns';
import fileSize from 'filesize';

function numberRound(value: number, fractions: number): string {
  return value.toFixed(fractions);
}

function formatIso(iso: string): string {
  const dt = parseISO(iso);
  return formatDate(dt, 'do MMMM yyyy HH:mm:ss');
}

i18next.use(initReactI18next).init({
  lng: 'nl',
  debug: isDevelopmentEnv(),
  resources: { nl: { translation } },
  interpolation: {
    format: (value, format) => {
      if (value === null || value === undefined) {
        return value;
      }

      switch (format) {
        case 'numberRound':
          return numberRound(value, 0);
        case 'numberRound2':
          return numberRound(value, 2);
        case 'isoDate':
          return formatIso(value);
        case 'fileSize':
          return fileSize(value);
        default:
          return value;
      }
    },
  },
});
