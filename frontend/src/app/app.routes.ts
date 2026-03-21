import { Routes } from '@angular/router';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { HistoryComponent } from './pages/history/history.component';
import { LoginComponent } from './pages/login/login.component';
import { ShareViewComponent } from './pages/share/share-view.component';
import { LandingComponent } from './pages/landing/landing.component';
import { CompareComponent } from './pages/compare/compare.component';
import { ApiDocsComponent } from './pages/api-docs/api-docs.component';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
    { path: '', component: LandingComponent },
    { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
    { path: 'history', component: HistoryComponent, canActivate: [authGuard] },
    { path: 'compare', component: CompareComponent, canActivate: [authGuard] },
    { path: 'login', component: LoginComponent },
    { path: 'register', component: LoginComponent },
    { path: 'api-docs', component: ApiDocsComponent },
    { path: 'share/:shareToken', component: ShareViewComponent },
    { path: '**', redirectTo: '' }
];
