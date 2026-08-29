import { FormEvent, useState } from 'react'
import { LogIn } from 'lucide-react'
import { guardarSesion } from '../services/api'

interface LoginProps {
    onLoginCorrecto: () => void
    onIrAEmpadronamiento: () => void
}

interface LoginResponse {
    token: string
    tipo: string
    expiraEnSegundos: number
    cui: string
    nombre: string
    padronId: number
}

export default function Login({
                                  onLoginCorrecto,
                                  onIrAEmpadronamiento
                              }: LoginProps) {
    const [cui, setCui] = useState('')
    const [password, setPassword] = useState('')
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')

    const handleSubmit = async (
        event: FormEvent<HTMLFormElement>
    ) => {
        event.preventDefault()

        setError('')
        setLoading(true)

        try {
            const respuesta = await fetch('/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    cui: cui.trim(),
                    password
                })
            })

            if (!respuesta.ok) {
                const contenido = await respuesta.text()
                let mensaje = 'CUI o contraseña incorrectos'

                if (contenido) {
                    try {
                        const errorBackend = JSON.parse(contenido)

                        mensaje =
                            errorBackend.mensaje ??
                            errorBackend.message ??
                            mensaje
                    } catch {
                        mensaje = contenido
                    }
                }

                throw new Error(mensaje)
            }

            const datos: LoginResponse =
                await respuesta.json()

            if (!datos.token) {
                throw new Error(
                    'El servidor no devolvió el token de acceso'
                )
            }

            guardarSesion({
                token: datos.token,
                nombre: datos.nombre,
                cui: datos.cui
            })

            onLoginCorrecto()
        } catch (error) {
            setError(
                error instanceof Error
                    ? error.message
                    : 'No fue posible iniciar sesión'
            )
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="min-h-screen bg-gray-100 flex items-center justify-center px-4">
            <div className="w-full max-w-md">
                <form
                    onSubmit={handleSubmit}
                    className="bg-white rounded-xl shadow-lg p-8"
                >
                    <h1 className="text-3xl font-bold text-gray-900">
                        Padrón Electoral
                    </h1>

                    <p className="mt-2 mb-8 text-gray-600">
                        Ingresa con el CUI y contraseña utilizados
                        durante el empadronamiento.
                    </p>

                    <div className="space-y-4">
                        <div>
                            <label
                                htmlFor="cui"
                                className="block mb-1 text-sm font-medium text-gray-700"
                            >
                                CUI
                            </label>

                            <input
                                id="cui"
                                type="text"
                                value={cui}
                                onChange={(event) =>
                                    setCui(event.target.value)
                                }
                                placeholder="Ingresa tu CUI"
                                autoComplete="username"
                                inputMode="numeric"
                                required
                                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-900"
                            />
                        </div>

                        <div>
                            <label
                                htmlFor="password"
                                className="block mb-1 text-sm font-medium text-gray-700"
                            >
                                Contraseña
                            </label>

                            <input
                                id="password"
                                type="password"
                                value={password}
                                onChange={(event) =>
                                    setPassword(event.target.value)
                                }
                                placeholder="Ingresa tu contraseña"
                                autoComplete="current-password"
                                required
                                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-900"
                            />
                        </div>
                    </div>

                    {error && (
                        <div className="mt-4 p-3 bg-red-100 text-red-700 rounded-lg text-sm">
                            {error}
                        </div>
                    )}

                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full mt-6 bg-gray-900 text-white font-medium py-3 rounded-lg hover:bg-gray-800 transition disabled:opacity-50 flex items-center justify-center gap-2"
                    >
                        <LogIn size={18}/>

                        {loading
                            ? 'Iniciando sesión...'
                            : 'Iniciar sesión'}
                    </button>

                    <button
                        type="button"
                        onClick={onIrAEmpadronamiento}
                        className="w-full mt-3 border border-gray-300 text-gray-700 font-medium py-3 rounded-lg hover:bg-gray-50 transition"
                    >
                        Crear nuevo empadronamiento
                    </button>
                </form>
            </div>
        </div>
    )
}