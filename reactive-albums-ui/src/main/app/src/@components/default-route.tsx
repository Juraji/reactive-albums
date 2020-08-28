import React from 'react';
import { Redirect, Route } from 'react-router-dom';

interface DefaultRouteProps {
  to: string;
  push?: boolean;
}

export function DefaultRoute({ to, push }: DefaultRouteProps) {
  return (
    <Route
      render={({ match }) => {
        const base = match.url.length > 1 ? match.url : '';
        return <Redirect to={`${base}${to}`} push={push || false} />;
      }}
    />
  );
}
