import { useAuth } from '@clerk/nextjs';
import { useCallback } from 'react';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080';

export function useApiClient() {
  const { getToken } = useAuth();

  const request = useCallback(async <T>(
    endpoint: string,
    options: RequestInit = {}
  ): Promise<T> => {
    const url = `${API_BASE_URL}${endpoint}`;
    const token = await getToken();
    
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` }),
      ...options.headers,
    };

    const config: RequestInit = {
      ...options,
      headers,
    };

    const response = await fetch(url, config);
    
    if (!response.ok) {
      throw new Error(`API request failed: ${response.status} ${response.statusText}`);
    }
    
    return response.json();
  }, [getToken]);

  const get = useCallback(async <T>(endpoint: string): Promise<T> => {
    return request<T>(endpoint, { method: 'GET' });
  }, [request]);

  const post = useCallback(async <T>(endpoint: string, data?: any): Promise<T> => {
    return request<T>(endpoint, {
      method: 'POST',
      body: data ? JSON.stringify(data) : undefined,
    });
  }, [request]);

  const put = useCallback(async <T>(endpoint: string, data?: any): Promise<T> => {
    return request<T>(endpoint, {
      method: 'PUT',
      body: data ? JSON.stringify(data) : undefined,
    });
  }, [request]);

  const del = useCallback(async <T>(endpoint: string): Promise<T> => {
    return request<T>(endpoint, { method: 'DELETE' });
  }, [request]);

  return {
    get,
    post,
    put,
    delete: del,
    request,
  };
} 