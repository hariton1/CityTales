export interface TourEntity {
  id: number,
  name: string,
  description: string,
  start_lat: number,
  start_lng: number,
  end_lat: number,
  end_lng: number,
  stops: string,
  distance: number,
  durationEstimate: number,
  userId: string
}
