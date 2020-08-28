import React, { useEffect, useMemo, useState } from 'react';
import { AppearanceType, ToastAdapterComponent } from 'react-toast-notifications';
import Toast from 'react-bootstrap/Toast';
import formatDistanceToNow from 'date-fns/formatDistanceToNow';
import nlLocale from 'date-fns/locale/nl';

const appearanceClassNames: Record<AppearanceType, string> = {
  error: 'bg-danger text-white',
  info: 'bg-info text-white',
  success: 'bg-success text-white',
  warning: 'bg-warning text-white',
};

function formatRelativeTimeAgo(spawnTime: Date): string {
  return formatDistanceToNow(spawnTime, { locale: nlLocale, includeSeconds: true, addSuffix: true });
}

export const BootstrapToastAdapter: ToastAdapterComponent = ({
  appearance,
  children,
  onDismiss,
  autoDismiss,
  autoDismissTimeout,
  transitionState,
}) => {
  const [spawnTime] = useState(new Date());
  const [relTimeStr, setRelTimeStr] = useState(() => formatRelativeTimeAgo(spawnTime));
  const headerClassName = useMemo(() => (!!appearance ? appearanceClassNames[appearance] : undefined), [appearance]);
  const showState = useMemo(() => transitionState === 'entered', [transitionState]);

  useEffect(() => {
    const tid = setInterval(() => setRelTimeStr(formatRelativeTimeAgo(spawnTime)), 1e4);
    return () => clearInterval(tid);
  }, [spawnTime]);

  useEffect(() => {
    if (autoDismiss) {
      const tid = (setTimeout(onDismiss, autoDismissTimeout) as unknown) as number;
      return () => clearTimeout(tid);
    }
  }, [autoDismiss, autoDismissTimeout, onDismiss]);

  return (
    <Toast onClose={onDismiss} show={showState} className="shadow-sm">
      <Toast.Header className={headerClassName}>
        <span className="mr-auto">&nbsp;</span>
        <small>{relTimeStr}</small>
      </Toast.Header>
      <Toast.Body>{children}</Toast.Body>
    </Toast>
  );
};
