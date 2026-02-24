export interface ApiResponse<T> {
  statusCode: number;
  message: string;
  errors: string[] | null;
  data: T; 
  timestamp: number;
  path: string;
}