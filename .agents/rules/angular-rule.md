---
trigger: always_on
description: Angular 19+ Best Practices - Front-End Guide
globs: angular.json, *.ts, *.html, src/**/*.ts, src/**/*.html
alwaysApply: false
---

# Angular 19+ Best Practices – Front-End Guide

> ✅ This guide consolidates best practices from the community (Cursor) and official Angular documentation (v20), providing a strict, scalable, and performance-driven approach.

---

## 🔧 TypeScript Best Practices

- **Enable Strict Typing** (`strict: true` in `tsconfig.json`)
- **Avoid `any`**  
  Use specific types or `unknown` when uncertain; never fallback to `any`.
- **Prefer Type Inference**  
  ```ts
  // ❌ Bad
  let name: string = 'Angular';

  // ✅ Good
  let name = 'Angular';
  ```
- **Use Interfaces for Models**
  Define domain models with `interface` or `type` for better clarity and maintainability.
- **Leverage Optional Chaining and Nullish Coalescing**  
  ```ts
  const displayName = user?.name ?? 'Anonymous';
  ```

---

## 🧱 Angular Architecture & Component Design

### ✅ Standalone Components First
- Always use **standalone components, directives, and pipes**.
- You can omit `standalone: true` (implied when generating standalone components in Angular 17+).

### ✅ Change Detection Strategy
Use `ChangeDetectionStrategy.OnPush` **by default** to minimize re-renders.

### ✅ Component Composition
Favor **composition** over inheritance to encourage reusability.

### ✅ Single Responsibility
Keep components **small, focused**, and responsible for **one task**.

---

## 💬 Inputs & Outputs

Use the **new function-based syntax** introduced in Angular 17+:

```ts
// ❌ Bad
@Input() userId!: string;
@Output() userSelected = new EventEmitter<string>();

// ✅ Good
import { input, output } from '@angular/core';

userId = input<string>('');
userSelected = output<string>();
```

---

## 🔁 State Management (Component-Level)

- Use **Signals** for local reactive state.
- Use `computed()` for derived state.
- Keep transformations **pure** and predictable.

---

## 🧠 Templates & View Logic

- Use **native control flow syntax** (`@if`, `@for`, `@switch`)  
  ```html
  @if (isVisible) {
    <div>Visible</div>
  }

  @for (item of items; track item.id) {
    <div>{{ item.name }}</div>
  }
  ```

- **Avoid complex logic in templates** – keep them clean and delegate logic to the component class.

- **Use `async` pipe** for observable binding, avoid manual subscription.

---

## 🎨 Styling

- **Prefer class/style bindings over `ngClass` / `ngStyle`**:
  ```html
  // ❌ Bad
  <div [ngClass]="{'active': isActive}"></div>

  // ✅ Good
  <div [class.active]="isActive"></div>
  ```

  ```html
  // ❌ Bad
  <div [ngStyle]="{'font-size': fontSize + 'px'}"></div>

  // ✅ Good
  <div [style.font-size.px]="fontSize"></div>
  ```

- **Use semantic HTML** and **ARIA attributes** for accessibility.

---

## 🖼️ Media & Performance

- Use `NgOptimizedImage` for all images.
- Lazy load non-critical views using **deferrable views** or `*ngIf` with suspense logic.
- Use `trackBy` in `@for` loops for list rendering:
  ```html
  @for (user of users; track user.id) {
    <app-user-card [user]="user" />
  }
  ```

---

## 🧪 Testing Best Practices

- Use **Arrange-Act-Assert** pattern for clarity:
  ```ts
  // ❌ Bad
  const service = new MyService();

  // ✅ Good
  const service = TestBed.inject(MyService);
  const result = service.doSomething();
  expect(result).toBe(expected);
  ```

- Ensure **unit test coverage** for components, services, and pure functions.
- Write **pure pipes** and test them independently.

---

## 💉 Dependency Injection

- Prefer `inject()` function over constructor injection when outside of a class context:
  ```ts
  // ❌ Bad
  constructor(private http: HttpClient) {}

  // ✅ Good
  import { inject } from '@angular/core';

  const http = inject(HttpClient);
  ```

- Services should have `providedIn: 'root'` for tree-shakability.

---

## 📂 File & Folder Structure

- File naming: **kebab-case**
- Suffix conventions:
  - `*.component.ts`
  - `*.service.ts`
  - `*.directive.ts`
  - `*.pipe.ts`
  - `*.spec.ts`

- Suggested import order:
  1. Angular core modules
  2. RxJS
  3. Angular-specific modules
  4. Application core
  5. Shared libraries
  6. Environment imports
  7. Relative paths

---

## 🛡️ Security Best Practices

- Avoid `innerHTML` and rely on Angular's built-in sanitization.
- Always sanitize dynamic inputs properly.
- Validate all form inputs using **Reactive Forms** or **Custom Validators**.

---

## 🚀 Performance & Web Vitals

- Optimize for LCP, INP, CLS.
- Use:
  - `OnPush`
  - Signals
  - `NgOptimizedImage`
  - Lazy loading
  - TrackBy
- Avoid:
  - Manual DOM manipulation
  - Redundant re-renders

---

## ✅ Summary of Key Principles

- **Immutability**: Always prefer `const` where possible.
- **Naming**: Be descriptive and intentional (`isUserLoggedIn`, `fetchOrders()`, etc.).
- **Accessibility**: Don’t skip ARIA or keyboard navigation.
- **Reusability**: Think in terms of standalone, composable building blocks.
- **Simplicity**: Fewer side-effects = better apps.

---

## 📚 References

- [Angular v20 Documentation](https://angular.io/docs)
- [Angular Signals](https://angular.dev/guide/signals)
- [NgOptimizedImage](https://angular.io/api/common/NgOptimizedImage)