import i18next from 'i18next';
import { initReactI18next } from 'react-i18next';
import { isDevelopmentEnv } from '@utils';
import translation from './translation.json';
import { format as formatDate, parseISO } from 'date-fns';
import fileSize from 'filesize';

function numberRound(value: number, fractions: number): string {
  return value.toFixed(fractions);
}

function formatIso(iso: string | undefined, fmt: string): string {
  if (!!iso) {
    const dt = parseISO(iso);
    return formatDate(dt, fmt);
  } else {
    return '';
  }
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
        case 'isoFullDate':
          return formatIso(value, 'do MMMM yyyy HH:mm');
        case 'isoDate':
          return formatIso(value, 'do MMMM yyyy');
        case 'isoTime':
          return formatIso(value, 'HH:mm');
        case 'fileSize':
          return fileSize(value);
        default:
          return value;
      }
    },
  },
});
