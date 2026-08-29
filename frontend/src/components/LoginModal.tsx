import { useState } from 'react'
import type { FormEvent } from 'react'
import { LogIn, X } from 'lucide-react'
import { guardarSesion } from '../services/api'

interface LoginModalProps {
    abierto: boolean
    onCerrar: () => void
    onLoginCorrecto: () => void
}

interface LoginResponse {
    token: string
    tipo: string
    expiraEnSegundos: number
    cui: string
    nombre: string
    padronId: number
}

export default function LoginModal({
                                       abierto,
                                       onCerrar,
                                       onLoginCorrecto
                                   }: LoginModalProps) {
    const [cui, setCui] = useState('')
    const [password, setPassword] = useState('')
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')

    if (!abierto) {
        return null
    }

    const limpiarFormulario = () => {
        setCui('')
        setPassword('')
        setError('')
    }

    const cerrarModal = () => {
        if (loading) {
            return
        }

        limpiarFormulario()
        onCerrar()
    }

    const obtenerMensajeError = async (
        respuesta: Response
    ): Promise<string> => {
        const contenido = await respuesta.text()

        if (!contenido) {
            return 'CUI o contraseña incorrectos'
        }

        try {
            const errorBackend = JSON.parse(contenido)

            return (
                errorBackend.mensaje ??
                errorBackend.message ??
                'CUI o contraseña incorrectos'
            )
        } catch {
            return contenido
        }
    }

    const handleSubmit = async (
        event: FormEvent<HTMLFormElement>
    ) => {
        event.preventDefault()

        setError('')
        setLoading(true)

        try {
            const respuesta = await fetch(
                '/api/auth/login',
                {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        cui: cui.trim(),
                        password
                    })
                }
            )

            if (!respuesta.ok) {
                const mensaje =
                    await obtenerMensajeError(respuesta)

                throw new Error(mensaje)
            }

            const resultado: LoginResponse =
                await respuesta.json()

            if (!resultado.token) {
                throw new Error(
                    'El servidor no devolvió el token de acceso'
                )
            }

            guardarSesion({
                token: resultado.token,
                nombre: resultado.nombre,
                cui: resultado.cui
            })

            limpiarFormulario()
            onLoginCorrecto()
        } catch (errorCapturado) {
            setError(
                errorCapturado instanceof Error
                    ? errorCapturado.message
                    : 'No fue posible iniciar sesión'
            )
        } finally {
            setLoading(false)
        }
    }
    return (
        <div
            className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/60 px-4"
            role="dialog"
            aria-modal="true"
            aria-labelledby="titulo-login"
            onMouseDown={cerrarModal}
        >
            <div
                className="relative w-full max-w-md rounded-xl bg-white p-8 shadow-2xl"
                onMouseDown={(event) => event.stopPropagation()}
            >
                <button
                    type="button"
                    onClick={cerrarModal}
                    disabled={loading}
                    className="absolute right-4 top-4 rounded-lg p-2 text-gray-500 transition hover:bg-gray-100 hover:text-gray-900 disabled:cursor-not-allowed disabled:opacity-50"
                    aria-label="Cerrar formulario de inicio de sesión"
                >
                    <X size={20}/>
                </button>

                <h2
                    id="titulo-login"
                    className="text-2xl font-bold text-gray-900"
                >
                    Iniciar sesión
                </h2>

                <p className="mb-6 mt-2 text-sm text-gray-600">
                    Ingresa el CUI y la contraseña utilizados durante el
                    empadronamiento.
                </p>

                <form onSubmit={handleSubmit}>
                    <div className="space-y-4">
                        <div>
                            <label
                                htmlFor="login-cui"
                                className="mb-1 block text-sm font-medium text-gray-700"
                            >
                                CUI
                            </label>

                            <input
                                id="login-cui"
                                type="text"
                                value={cui}
                                onChange={(event) =>
                                    setCui(event.target.value)
                                }
                                placeholder="Ingresa tu CUI"
                                inputMode="numeric"
                                autoComplete="username"
                                required
                                autoFocus
                                disabled={loading}
                                className="w-full rounded-lg border border-gray-300 px-4 py-3 focus:outline-none focus:ring-2 focus:ring-gray-900 disabled:bg-gray-100 disabled:opacity-70"
                            />
                        </div>

                        <div>
                            <label
                                htmlFor="login-password"
                                className="mb-1 block text-sm font-medium text-gray-700"
                            >
                                Contraseña
                            </label>

                            <input
                                id="login-password"
                                type="password"
                                value={password}
                                onChange={(event) =>
                                    setPassword(event.target.value)
                                }
                                placeholder="Ingresa tu contraseña"
                                autoComplete="current-password"
                                required
                                disabled={loading}
                                className="w-full rounded-lg border border-gray-300 px-4 py-3 focus:outline-none focus:ring-2 focus:ring-gray-900 disabled:bg-gray-100 disabled:opacity-70"
                            />
                        </div>
                    </div>

                    {error && (
                        <div
                            className="mt-4 rounded-lg bg-red-100 p-3 text-sm text-red-700"
                            role="alert"
                        >
                            {error}
                        </div>
                    )}

                    <button
                        type="submit"
                        disabled={loading}
                        className="mt-6 flex w-full items-center justify-center gap-2 rounded-lg bg-gray-900 py-3 font-medium text-white transition hover:bg-gray-800 disabled:cursor-not-allowed disabled:opacity-50"
                    >
                        <LogIn size={18}/>

                        {loading
                            ? 'Iniciando sesión...'
                            : 'Iniciar sesión'}
                    </button>

                    <button
                        type="button"
                        onClick={cerrarModal}
                        disabled={loading}
                        className="mt-3 w-full rounded-lg border border-gray-300 py-3 font-medium text-gray-700 transition hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-50"
                    >
                        Cancelar
                    </button>
                </form>
            </div>
        </div>
    )
}