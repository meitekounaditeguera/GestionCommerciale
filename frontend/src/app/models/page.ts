// Représentation générique d'une réponse paginée Spring Boot (Page / PageImpl).
export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  // Index de la page courante, base 0 (comme côté Spring Boot).
  number: number;
  size: number;
}
