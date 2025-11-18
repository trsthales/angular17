import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CartComponent } from './cart.component';
import { CartStore } from '../core/cart.store';
import { UserIdService } from '../core/user-id.service';

describe('CartComponent', () => {
  let fixture: ComponentFixture<CartComponent>;

  const mockStore = {
    items: () => [{ productId: 'p1', productName: 'Produto A', unitPrice: 10, quantity: 1 }],
    total: () => 10,
    loading: () => false,
    update: jasmine.createSpy('update'),
    remove: jasmine.createSpy('remove'),
  } as unknown as CartStore;

  const mockUser = {
    userId: () => '00000000-0000-0000-0000-000000000001'
  } as unknown as UserIdService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CartComponent],
      providers: [
        { provide: CartStore, useValue: mockStore },
        { provide: UserIdService, useValue: mockUser },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(CartComponent);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should display item and call update when increase clicked', () => {
    const el: HTMLElement = fixture.nativeElement;
    expect(el.textContent).toContain('Produto A');

    const buttons = el.querySelectorAll('button');
    // order: decrease, increase, remove -> index 1 is increase
    const increaseBtn = buttons[1] as HTMLButtonElement;
    increaseBtn.click();

    expect(mockStore.update).toHaveBeenCalledWith('p1', 2, '00000000-0000-0000-0000-000000000001');
  });
});
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CartComponent } from './cart.component';

describe('CartComponent', () => {
  let fixture: ComponentFixture<CartComponent>;

  const mockStore = {
    items: () => [{ productId: 'p1', productName: 'Produto A', unitPrice: 10, quantity: 1 }],
    total: () => 10,
    loading: () => false,
    update: jasmine.createSpy('update'),
    remove: jasmine.createSpy('remove'),
  } as any;

  const mockUser = {
    userId: () => '00000000-0000-0000-0000-000000000001'
  } as any;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CartComponent],
      providers: [
        { provide: (CartComponent as any).ɵprov.deps?.[0], useValue: mockStore },
      ],
    })
      // Since CartComponent injects CartStore and UserIdService via `inject()`
      // and they are not easily referenceable here, we override providers
      // by token using the classes directly.
      .overrideProvider((<any>Object).getPrototypeOf(mockStore).constructor, { useValue: mockStore })
      .compileComponents();

    // Provide CartStore and UserIdService by name (fallback)
    TestBed.overrideProvider((<any>Object), { useValue: mockStore });

    // More reliable approach: explicitly provide by class names
    TestBed.resetTestingModule();
    await TestBed.configureTestingModule({
      imports: [CartComponent],
      providers: [
        { provide: (window as any).CartStore || 'CartStore', useValue: mockStore },
        { provide: (window as any).UserIdService || 'UserIdService', useValue: mockUser },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(CartComponent);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should display items and call update on increase', () => {
    const el: HTMLElement = fixture.nativeElement;
    expect(el.textContent).toContain('Produto A');

    const buttons = el.querySelectorAll('button');
    // botão de aumentar é o segundo botão por item (decrease, increase, remove)
    const increaseBtn = buttons[1] as HTMLButtonElement;
    increaseBtn.click();

    expect(mockStore.update).toHaveBeenCalledWith('p1', 2, '00000000-0000-0000-0000-000000000001');
  });
});
