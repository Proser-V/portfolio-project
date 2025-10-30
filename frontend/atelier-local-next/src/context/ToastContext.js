"use client";

import { createContext, useContext, useState, useCallback } from "react";

const ToastContext = createContext();

export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([]);

  const addToast = useCallback((message, type = "info", duration = 4000) => {
    const id = Date.now() + Math.random();
    const newToast = { id, message, type, isVisible: true };
    setToasts((prev) => [...prev, newToast]);

    setTimeout(() => {
      setToasts((prev) =>
        prev.map((t) => (t.id === id ? { ...t, isVisible: false } : t))
      );
    }, duration - 500);

    setTimeout(() => {
      setToasts((prev) => prev.filter((t) => t.id !== id));
    }, duration);
  }, []); // ← Pas de dépendances car on utilise des fonctions de mise à jour

  return (
    <ToastContext.Provider value={{ addToast }}>
      {children}
      <div 
        className="fixed top-5 flex flex-col gap-2 z-[9999]"
        style={{
            left: '50%',
            transform: 'translateX(-50%)'
        }}
      >
        {toasts.map((toast) => (
          <div
            key={toast.id}
            className={`
              px-6 py-3 rounded-lg shadow-md text-sm font-medium
              transition-opacity duration-500
              ${toast.type === "error" ? "bg-red-100 text-red-700 border border-red-200" : ""}
              ${toast.type === "success" ? "bg-green-100 text-green-700 border border-green-200" : ""}
              ${toast.type === "info" ? "bg-blue-100 text-blue-700 border border-blue-200" : ""}
            `}
            style={{ opacity: toast.isVisible ? 1 : 0 }}
          >
            {toast.message}
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
}

export const useToast = () => useContext(ToastContext);
