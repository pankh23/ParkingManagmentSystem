// Fix for ResizeObserver loop error
// This error occurs when ResizeObserver detects changes but can't deliver all notifications in a single frame
// It's a known issue with React and doesn't affect functionality

// Store original handlers
const originalErrorHandler = window.onerror;
const originalUnhandledRejectionHandler = window.onunhandledrejection;
const originalConsoleError = console.error;
const originalConsoleWarn = console.warn;

// Function to check if error is ResizeObserver related
const isResizeObserverError = (message) => {
  if (typeof message === 'string') {
    return message.includes('ResizeObserver loop completed with undelivered notifications');
  }
  return false;
};

// Override window.onerror
window.onerror = (message, source, lineno, colno, error) => {
  if (isResizeObserverError(message)) {
    // Suppress ResizeObserver errors completely
    return true;
  }
  // Call original handler for other errors
  if (originalErrorHandler) {
    return originalErrorHandler(message, source, lineno, colno, error);
  }
  return false;
};

// Override window.onunhandledrejection
window.onunhandledrejection = (event) => {
  if (event.reason && isResizeObserverError(event.reason.message)) {
    // Suppress ResizeObserver errors
    event.preventDefault();
    return;
  }
  // Call original handler for other errors
  if (originalUnhandledRejectionHandler) {
    return originalUnhandledRejectionHandler(event);
  }
};

// Override console.error
console.error = (...args) => {
  if (args.length > 0 && isResizeObserverError(args[0])) {
    // Suppress ResizeObserver errors
    return;
  }
  // Call original console.error for other errors
  originalConsoleError.apply(console, args);
};

// Override console.warn
console.warn = (...args) => {
  if (args.length > 0 && isResizeObserverError(args[0])) {
    // Suppress ResizeObserver errors
    return;
  }
  // Call original console.warn for other errors
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

// Override ResizeObserverEntry if it exists
if (window.ResizeObserverEntry) {
  const OriginalResizeObserverEntry = window.ResizeObserverEntry;
  // Keep the original but wrap any methods that might throw errors
}

// Suppress ResizeObserver errors globally
const suppressResizeObserverError = (error) => {
  if (error && error.message && isResizeObserverError(error.message)) {
    return true;
  }
  return false;
};

export default suppressResizeObserverError;
