import React from 'react';
import ReactDOM from 'react-dom/client';
import { QueryClient, QueryClientProvider } from 'react-query';
import 'antd/dist/antd.css';
import './index.css';
import './picker-styles.css'; // Clean picker styles
import './utils/resizeObserverFix'; // Fix ResizeObserver loop error
import App from './App';

// Suppress ResizeObserver errors before anything else loads
const originalError = window.onerror;
const originalUnhandledRejection = window.onunhandledrejection;
const originalConsoleError = console.error;
const originalConsoleWarn = console.warn;

// Function to check if error is ResizeObserver related
function isResizeObserverError(message) {
  if (typeof message === 'string') {
    return message.includes('ResizeObserver loop completed with undelivered notifications');
  }
  return false;
}

// Override window.onerror
window.onerror = function(message, source, lineno, colno, error) {
  if (isResizeObserverError(message)) {
    return true; // Suppress the error
  }
  if (originalError) {
    return originalError(message, source, lineno, colno, error);
  }
  return false;
};

// Override window.onunhandledrejection
window.onunhandledrejection = function(event) {
  if (event.reason && isResizeObserverError(event.reason.message)) {
    event.preventDefault();
    return;
  }
  if (originalUnhandledRejection) {
    return originalUnhandledRejection(event);
  }
};

// Override console.error
console.error = function(...args) {
  if (args[0] && isResizeObserverError(args[0])) {
    return; // Suppress the error
  }
  originalConsoleError.apply(console, args);
};

// Override console.warn
console.warn = function(...args) {
  if (args[0] && isResizeObserverError(args[0])) {
    return; // Suppress the error
  }
  originalConsoleWarn.apply(console, args);
};

// Override Error constructor
const OriginalError = window.Error;
window.Error = function(message) {
  if (isResizeObserverError(message)) {
    // Create a silent error
    const error = new OriginalError();
    error.message = message;
    error.name = 'ResizeObserverError';
    error.stack = '';
    return error;
  }
  return new OriginalError(message);
};

// Override Error constructor on prototype
window.Error.prototype = OriginalError.prototype;

// Override Error.captureStackTrace if it exists
if (Error.captureStackTrace) {
  const originalCaptureStackTrace = Error.captureStackTrace;
  Error.captureStackTrace = function(obj, func) {
    if (obj && obj.message && isResizeObserverError(obj.message)) {
      return;
    }
    return originalCaptureStackTrace.call(this, obj, func);
  };
}

// Add event listener for error events
window.addEventListener('error', (event) => {
  if (isResizeObserverError(event.message)) {
    event.preventDefault();
    event.stopPropagation();
    return false;
  }
});

// Add event listener for unhandledrejection events
window.addEventListener('unhandledrejection', (event) => {
  if (event.reason && isResizeObserverError(event.reason.message)) {
    event.preventDefault();
    event.stopPropagation();
    return false;
  }
});

// Override ResizeObserver constructor to suppress errors
if (window.ResizeObserver) {
  const OriginalResizeObserver = window.ResizeObserver;
  window.ResizeObserver = function(callback) {
    const wrappedCallback = (entries, observer) => {
      try {
        callback(entries, observer);
      } catch (error) {
        if (isResizeObserverError(error.message)) {
          // Suppress ResizeObserver errors
          return;
        }
        throw error;
      }
    };
    return new OriginalResizeObserver(wrappedCallback);
  };
  // Copy static properties
  Object.setPrototypeOf(window.ResizeObserver, OriginalResizeObserver);
  Object.defineProperty(window.ResizeObserver, 'prototype', {
    value: OriginalResizeObserver.prototype,
    writable: false
  });
}

// Override ResizeObserver methods to suppress errors
if (window.ResizeObserver) {
  const OriginalResizeObserver = window.ResizeObserver;
  const originalObserve = OriginalResizeObserver.prototype.observe;
  const originalUnobserve = OriginalResizeObserver.prototype.unobserve;
  const originalDisconnect = OriginalResizeObserver.prototype.disconnect;
  
  OriginalResizeObserver.prototype.observe = function(target, options) {
    try {
      return originalObserve.call(this, target, options);
    } catch (error) {
      if (isResizeObserverError(error.message)) {
        return;
      }
      throw error;
    }
  };
  
  OriginalResizeObserver.prototype.unobserve = function(target) {
    try {
      return originalUnobserve.call(this, target);
    } catch (error) {
      if (isResizeObserverError(error.message)) {
        return;
      }
      throw error;
    }
  };
  
  OriginalResizeObserver.prototype.disconnect = function() {
    try {
      return originalDisconnect.call(this);
    } catch (error) {
      if (isResizeObserverError(error.message)) {
        return;
      }
      throw error;
    }
  };
}

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      refetchOnWindowFocus: false,
    },
  },
});

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <QueryClientProvider client={queryClient}>
    <App />
  </QueryClientProvider>
);
