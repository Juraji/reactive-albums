import React, { FC, ReactElement } from 'react';

interface ConditionalProps {
  condition: boolean;
  orElse?: ReactElement;
}

export const Conditional: FC<ConditionalProps> = ({ condition, orElse, children }) =>
  condition ? <>{children}</> : orElse || <></>;
