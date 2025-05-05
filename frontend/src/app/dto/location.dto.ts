import {StoryDto} from './story.dto';

export class LocationDto{
  private readonly id: number;
  private readonly name: string;
  private readonly latitude: number;
  private readonly longitude: number;
  private readonly stories: StoryDto[];
  private prices: number[];

  constructor(name: string, latitude: number, longitude: number) {
    this.id = -1;
    this.name = name;
    this.latitude = latitude;
    this.longitude = longitude;
    this.stories = [];
    this.prices = [];
  }

  add(story: StoryDto) {
    this.stories.push(story);
  }

  remove(story: StoryDto) {
    this.stories.splice(this.stories.indexOf(story), 1);
  }

  addPrice(price: number) {
    this.prices.push(price);
  }

  removePrice(price: number) {
    this.prices.splice(this.prices.indexOf(price), 1);
  }
}
