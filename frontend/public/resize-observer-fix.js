// ResizeObserver error suppression script
// This runs before React loads to catch errors early

(function() {
  'use strict';
  
  // Store original handlers
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
    if (args.length > 0 && isResizeObserverError(args[0])) {
      return; // Suppress ResizeObserver errors
    }
    originalConsoleError.apply(console, args);
  };
  
  // Override console.warn
  console.warn = function(...args) {
    if (args.length > 0 && isResizeObserverError(args[0])) {
      return; // Suppress ResizeObserver errors
    }
    originalConsoleWarn.apply(console, args);
  };
  
  // Add event listeners
  window.addEventListener('error', function(event) {
    if (isResizeObserverError(event.message)) {
      event.preventDefault();
      event.stopPropagation();
      return false;
    }
  });
  
  window.addEventListener('unhandledrejection', function(event) {
    if (event.reason && isResizeObserverError(event.reason.message)) {
      event.preventDefault();
      event.stopPropagation();
      return false;
    }
  });
  
  // Override ResizeObserver constructor
  if (window.ResizeObserver) {
    const OriginalResizeObserver = window.ResizeObserver;
    window.ResizeObserver = function(callback) {
      const wrappedCallback = function(entries, observer) {
        try {
          callback(entries, observer);
        } catch (error) {
          if (isResizeObserverError(error.message)) {
            return; // Suppress ResizeObserver errors
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
  
  console.log('ResizeObserver error suppression loaded');
})();
