import { useApiClient } from '@/hooks/use-api-client';

// Types for your Spring Boot API
export interface Receipt {
  id: string;
  retailer: string;
  purchaseDate: string;
  purchaseTime: string;
  total: number;
  items: ReceiptItem[];
  pointsEarned: number;
  status: 'processed' | 'pending' | 'failed';
}

export interface ReceiptItem {
  shortDescription: string;
  price: number;
}

export interface UploadReceiptRequest {
  imageData: string; // base64 encoded image
  merchant?: string;
  date?: string;
}

export interface UploadReceiptResponse {
  receipt: Receipt;
  pointsEarned: number;
}

export interface UserPoints {
  totalPoints: number;
  pointsThisMonth: number;
  receiptsThisMonth: number;
}

// Custom hook for receipt operations
export function useReceiptService() {
  const api = useApiClient();

  const uploadReceipt = async (imageFile: File): Promise<UploadReceiptResponse> => {
    // Convert file to base64
    const base64 = await new Promise<string>((resolve) => {
      const reader = new FileReader();
      reader.onload = () => resolve(reader.result as string);
      reader.readAsDataURL(imageFile);
    });

    const request: UploadReceiptRequest = {
      imageData: base64,
    };

    return api.post<UploadReceiptResponse>('/api/receipts/upload', request);
  };

  const getReceipts = async (): Promise<Receipt[]> => {
    return api.get<Receipt[]>('/api/receipts');
  };

  const getReceipt = async (id: string): Promise<Receipt> => {
    return api.get<Receipt>(`/api/receipts/${id}`);
  };

  const getUserPoints = async (): Promise<UserPoints> => {
    return api.get<UserPoints>('/api/user/points');
  };

  const deleteReceipt = async (id: string): Promise<void> => {
    return api.delete<void>(`/api/receipts/${id}`);
  };

  return {
    uploadReceipt,
    getReceipts,
    getReceipt,
    getUserPoints,
    deleteReceipt,
  };
} 