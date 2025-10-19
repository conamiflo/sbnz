import { RecommendationDTO } from './recommendation-dto.model'; // Увези RecommendationDTO

export interface RecommendationResponse {
  success: boolean;
  message: string;
  recommendations: RecommendationDTO[]; // Листа препорука
  recommendationsByUser: { [userId: string]: RecommendationDTO[] };
  totalCount: number; // int -> number
  averagePriorityScore: number; // double -> number
  highPriorityCount: number; // long -> number
}
