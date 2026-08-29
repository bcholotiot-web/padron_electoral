const TOKEN_KEY = 'padron_jwt'
const NOMBRE_KEY = 'padron_usuario_nombre'
const CUI_KEY = 'padron_usuario_cui'

export interface DatosSesion {
    token: string
    nombre: string
    cui: string
}

export function guardarSesion(datos: DatosSesion): void {
    sessionStorage.setItem(TOKEN_KEY, datos.token)
    sessionStorage.setItem(NOMBRE_KEY, datos.nombre)
    sessionStorage.setItem(CUI_KEY, datos.cui)
}

export function obtenerToken(): string | null {
    return sessionStorage.getItem(TOKEN_KEY)
}

export function obtenerNombreUsuario(): string {
    return sessionStorage.getItem(NOMBRE_KEY) ?? ''
}

export function obtenerCuiUsuario(): string {
    return sessionStorage.getItem(CUI_KEY) ?? ''
}

export function existeSesion(): boolean {
    return Boolean(obtenerToken())
}

export function cerrarSesion(): void {
    sessionStorage.removeItem(TOKEN_KEY)
    sessionStorage.removeItem(NOMBRE_KEY)
    sessionStorage.removeItem(CUI_KEY)
}

export async function apiFetch(
    ruta: string,
    opciones: RequestInit = {}
): Promise<Response> {
    const token = obtenerToken()
    const headers = new Headers(opciones.headers)

    if (opciones.body && !headers.has('Content-Type')) {
        headers.set('Content-Type', 'application/json')
    }

    if (token) {
        headers.set(
            'Authorization',
            `Bearer ${token}`
        )
    }

    const respuesta = await fetch(ruta, {
        ...opciones,
        headers
    })

    if (respuesta.status === 401) {
        cerrarSesion()

        window.dispatchEvent(
            new CustomEvent('sesion-expirada')
        )
    }

    return respuesta
}