import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExploreLayoutComponent } from './explore-layout.component';

describe('ExploreLayoutComponent', () => {
  let component: ExploreLayoutComponent;
  let fixture: ComponentFixture<ExploreLayoutComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExploreLayoutComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ExploreLayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
