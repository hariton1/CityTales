export class StoryDto {
  private readonly name: string;
  private description: string;
  private readonly images: string[]
  private readonly relatedStories: StoryDto[]
  private readonly relatedPeople: string[]

  constructor(name: string, description: string) {
    this.name = name;
    this.description = description;
    this.images = [];
    this.relatedStories = [];
    this.relatedPeople = [];
  }

  getName(): string {
    return this.name;
  }

  setDescription(description: string) {
    this.description = description;
  }

  getDescription() {
    return this.description;
  }

  addImage(image: string) {
    this.images.push(image);
  }

  removeImage(image: string) {
    this.images.splice(this.images.indexOf(image), 1);
  }

  addRelatedStory(story: StoryDto) {
    this.relatedStories.push(story);
  }

  removeRelatedStory(story: StoryDto) {
    this.relatedStories.splice(this.relatedStories.indexOf(story), 1);
  }

  addRelatedPeople(people: string) {
    this.relatedPeople.push(people);
  }

  removeRelatedPeople(people: string) {
    this.relatedPeople.splice(this.relatedPeople.indexOf(people), 1);
  }
}
