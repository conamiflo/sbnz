import { UserDTO } from './user-dto.model'; // Увези UserDTO
export interface RecommendationDTO {
  content: string;
  reasoning: string; // Име поља је 'reasoning' у Recommendation.java, па га користимо
  priorityScore: number; // double -> number
  user: UserDTO | null; // Препорука може бити везана за корисника
}
