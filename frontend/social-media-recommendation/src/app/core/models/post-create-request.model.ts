export interface PostCreateRequest {
  content: string;
  contentType: string;
  category: string;
  hashtags?: string[] | null;
}
