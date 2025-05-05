import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {SERVER_ADDRESS} from '../globals';
import {StoryDto} from '../dto/story.dto';
import {Injectable} from '@angular/core';

@Injectable({ providedIn: 'root' })
export class StoryService {

  constructor(private httpClient: HttpClient) {
  }

  public createStory(storyDto: StoryDto): Observable<StoryDto> {
    return this.httpClient.post<StoryDto>(SERVER_ADDRESS + 'story/create', {
      params: [],
      data: storyDto
    });
  }

  public readStory(storyId: number): Observable<StoryDto> {
    return this.httpClient.get<StoryDto>(SERVER_ADDRESS + 'story/read' + storyId);
  }

  public updateStory(storyDto: StoryDto): Observable<StoryDto> {
    return this.httpClient.put<StoryDto>(SERVER_ADDRESS + 'story/update', {
      params: [],
      data: storyDto
    });
  }

  public deleteStory(storyId: number): Observable<boolean> {
    return this.httpClient.delete<boolean>(SERVER_ADDRESS + 'story/delete/' + storyId);
  }
}
