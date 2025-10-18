export interface PostResponse {
  id: number;
  username: string;
  content: string;
  contentType: string;
  category: string;
  hashtags?: string[] | null;
  publishTime: string;
}
