export interface Post {
  id: number;
  username: string;
  content: string;
  contentType: string;
  category: string;
  hashtags?: string[] | null;
  publishTime: string;
  likes: number;
  comments: number;
  shares: number;
  reach?: number | null;
  engagementRate?: number | null;
}
