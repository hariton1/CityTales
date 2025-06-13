import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FunFactListComponent } from './fun-fact-list.component';

describe('FunFactListComponent', () => {
  let component: FunFactListComponent;
  let fixture: ComponentFixture<FunFactListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FunFactListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FunFactListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
