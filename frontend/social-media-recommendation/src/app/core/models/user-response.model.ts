export interface UserResponse {
  id: number;
  username: string;
  name: string;
  age?: number | null;
  location?: string | null;
  gender?: string | null;
  interests?: string[];
  creatorType?: string | null;
  audienceSize?: number | null;
}
