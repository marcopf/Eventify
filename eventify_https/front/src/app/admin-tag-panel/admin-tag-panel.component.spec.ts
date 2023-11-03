import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminTagPanelComponent } from './admin-tag-panel.component';

describe('AdminTagPanelComponent', () => {
  let component: AdminTagPanelComponent;
  let fixture: ComponentFixture<AdminTagPanelComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AdminTagPanelComponent]
    });
    fixture = TestBed.createComponent(AdminTagPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
