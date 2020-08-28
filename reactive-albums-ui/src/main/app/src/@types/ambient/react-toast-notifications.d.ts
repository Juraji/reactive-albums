declare module 'react-toast-notifications' {
  import React, { ReactNode } from 'react';

  type AppearanceType = 'error' | 'info' | 'success' | 'warning';
  type ToastId = string;
  type ToastDismissCallback = () => void;
  type Options = {
    appearance: AppearanceType;
    autoDismiss?: boolean;
    onDismiss?: ToastDismissCallback;
  };

  type TransitionState = 'entering' | 'entered' | 'exiting' | 'exited';
  type PlacementType = 'bottom-left' | 'bottom-center' | 'bottom-right' | 'top-left' | 'top-center' | 'top-right';
  type ToastType = Options & { appearance: AppearanceType; content: Node; id: ToastId };
  type ToastsType = Array<ToastType>;

  type ToastAdapterComponent = React.FC<ToastProps>;
  type ToastContainerAdapterComponent = React.FC<ToastContainerProps>;

  interface ToastProviderState {
    toasts: ToastsType;
  }

  interface ToastHook {
    addToast: (content: React.ReactNode, options?: Options) => ToastId;
    removeToast: (id: ToastId) => void;
    removeAllToasts: () => void;
    updateToast: (id: ToastId, options: Options) => void;
    toastStack: ToastsType;
  }

  interface ToastProviderProps {
    children: ReactNode;
    autoDismissTimeout?: number;
    components?: {
      ToastContainer?: ToastContainerAdapterComponent;
      Toast?: ToastAdapterComponent;
    };
    placement?: PlacementType;
    transitionDuration?: number;
  }

  interface ToastProps {
    children: ReactNode;
    appearance?: AppearanceType;
    autoDismiss: boolean;
    autoDismissTimeout: number;
    onDismiss: () => void;
    placement: PlacementType;
    transitionDuration: number;
    transitionState: TransitionState;
  }

  interface ToastContainerProps {}

  export const ToastProvider: React.FC<ToastProviderProps, ToastProviderState>;
  export function useToasts(): ToastHook;
}
