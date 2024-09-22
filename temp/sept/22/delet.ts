import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DeleteRendererComponent } from './delete-renderer.component';
import { ICellRendererParams } from 'ag-grid-community';
import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';

describe('DeleteRendererComponent', () => {
  let component: DeleteRendererComponent;
  let fixture: ComponentFixture<DeleteRendererComponent>;
  let debugElement: DebugElement;
  
  // Mock data for the params
  const paramsMock: ICellRendererParams = {
    data: {
      reviewStatus: '',
      sid: '123',
      isRestrictedUser: true,
      isDocManager: false,
      isGrp: false
    },
    context: {
      componentParent: {
        deleteGridRow: jasmine.createSpy('deleteGridRow') // Mock delete method
      }
    }
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DeleteRendererComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(DeleteRendererComponent);
    component = fixture.componentInstance;
    debugElement = fixture.debugElement;
  });

  // Test if component is created
  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  // Test agInit method
  it('should initialize with provided params', () => {
    component.agInit(paramsMock);
    expect(component.params).toEqual(paramsMock);
  });

  // Test disabling logic when reviewStatus is "Feedback response submitted" or "Feedback response reviewed"
  it('should disable the button if reviewStatus is "Feedback response submitted" or "Feedback response reviewed"', () => {
    paramsMock.data.reviewStatus = 'Feedback response submitted';
    component.agInit(paramsMock);
    expect(component.disabled).toBeTrue();

    paramsMock.data.reviewStatus = 'Feedback response reviewed';
    component.agInit(paramsMock);
    expect(component.disabled).toBeTrue();
  });

  // Test enabling logic for other statuses
  it('should not disable the button for other statuses', () => {
    paramsMock.data.reviewStatus = 'Other status';
    component.agInit(paramsMock);
    expect(component.disabled).toBeFalse();
  });

  // Test onClick method
  it('should call deleteGridRow from parent context when clicked', () => {
    component.agInit(paramsMock);
    component.onClick({});
    expect(paramsMock.context.componentParent.deleteGridRow).toHaveBeenCalledWith(paramsMock.data);
  });

  // Test HTML rendering and click event
  it('should have clickable icon when not disabled', () => {
    paramsMock.data.reviewStatus = 'Other status';
    component.agInit(paramsMock);
    fixture.detectChanges(); // Update HTML

    const iconElement = debugElement.query(By.css('.crsr'));
    expect(iconElement).toBeTruthy(); // Check if element is present

    iconElement.triggerEventHandler('click', null);
    expect(paramsMock.context.componentParent.deleteGridRow).toHaveBeenCalled();
  });

  it('should not allow click when disabled', () => {
    paramsMock.data.reviewStatus = 'Feedback response submitted';
    component.agInit(paramsMock);
    fixture.detectChanges(); // Update HTML

    const iconElement = debugElement.query(By.css('.crsr'));
    expect(iconElement).toBeTruthy(); // Check if element is present

    iconElement.triggerEventHandler('click', null);
    expect(paramsMock.context.componentParent.deleteGridRow).not.toHaveBeenCalled(); // Should not call delete when disabled
  });
});
