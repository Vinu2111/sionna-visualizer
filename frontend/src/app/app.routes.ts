import { Routes } from '@angular/router';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { HistoryComponent } from './pages/history/history.component';
import { LoginComponent } from './pages/login/login.component';
import { ShareViewComponent } from './pages/share/share-view.component';
import { LandingComponent } from './pages/landing/landing.component';
import { CompareComponent } from './pages/compare/compare.component';
import { ApiDocsComponent } from './pages/api-docs/api-docs.component';
import { authGuard } from './guards/auth.guard';

// ── Lazy-loaded feature pages ──────────────────────────────────────────────
// These are loaded on demand so the initial bundle stays small.
// Each page import resolves only when the user navigates to that route.

export const routes: Routes = [
    { path: '', component: LandingComponent },
    { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
    { path: 'history', component: HistoryComponent, canActivate: [authGuard] },
    { path: 'compare', component: CompareComponent, canActivate: [authGuard] },
    { path: 'login', component: LoginComponent },
    { path: 'register', component: LoginComponent },
    { path: 'api-docs', component: ApiDocsComponent },
    { path: 'share/:shareToken', component: ShareViewComponent },

    // Natural Language Simulation — AI-powered parameter extraction via Claude
    {
        path: 'dashboard/nl-simulation',
        canActivate: [authGuard],
        loadComponent: () =>
            import('./pages/nl-simulation/nl-simulation.component')
                .then(m => m.NlSimulationComponent)
    },

    // THz Atmospheric Link Simulator
    {
        path: 'dashboard/thz-atmospheric',
        canActivate: [authGuard],
        loadComponent: () =>
            import('./pages/thz-atmospheric/thz-atmospheric.component')
                .then(m => m.ThzAtmosphericComponent)
    },

    // TTDF Progress Dashboard — Government of India grant tracking
    {
        path: 'dashboard/ttdf',
        canActivate: [authGuard],
        loadComponent: () =>
            import('./pages/ttdf/ttdf-dashboard.component')
                .then(m => m.TtdfDashboardComponent)
    },

    // Bharat 6G Alliance — PoC tracking and quarterly reporting
    {
        path: 'dashboard/bharat-alliance',
        canActivate: [authGuard],
        loadComponent: () =>
            import('./pages/bharat-alliance/bharat-alliance-dashboard.component')
                .then(m => m.BharatAllianceDashboardComponent)
    },

    // Public Gallery — browsable without login
    {
        path: 'gallery',
        loadComponent: () =>
            import('./pages/gallery/public-gallery.component')
                .then(m => m.PublicGalleryComponent)
    },
    {
        path: 'gallery/:id',
        loadComponent: () =>
            import('./pages/gallery/gallery-detail.component')
                .then(m => m.GalleryDetailComponent)
    },
    {
        path: 'gallery/publish/:simulationId',
        canActivate: [authGuard],
        loadComponent: () =>
            import('./pages/gallery/publish-simulation.component')
                .then(m => m.PublishSimulationComponent)
    },

    // Experiment Dashboard — tag, star, note management
    {
        path: 'dashboard/experiments',
        canActivate: [authGuard],
        loadComponent: () =>
            import('./pages/experiments/experiment-dashboard.component')
                .then(m => m.ExperimentDashboardComponent)
    },

    // Collaborative Team Workspace
    {
        path: 'workspace',
        canActivate: [authGuard],
        loadComponent: () =>
            import('./pages/workspace/workspace-dashboard.component')
                .then(m => m.WorkspaceDashboardComponent)
    },
    {
        path: 'workspace/simulation/:id',
        canActivate: [authGuard],
        loadComponent: () =>
            import('./pages/workspace/workspace-detail.component')
                .then(m => m.WorkspaceDetailComponent)
    },

    { path: '**', redirectTo: '' }
];
