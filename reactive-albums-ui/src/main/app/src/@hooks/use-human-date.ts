import { useMemo } from 'react';
import { format, parseISO } from 'date-fns';

export function useHumanDate(date: Date | string | undefined): string {
  return useMemo(() => {
    if (!!date) {
      const dt = typeof date === 'string' ? parseISO(date) : date;
      return format(dt, 'do MMMM yyyy HH:mm:ss');
    } else {
      return '';
    }
  }, [date]);
}
