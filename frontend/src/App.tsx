import { useState, useEffect } from 'react'
import FormularioPadron from './components/FormularioPadron'
import TablaPadron from './components/TablaPadron'
import { LogIn } from 'lucide-react'
import LoginModal from './components/LoginModal'
import MiPadron from './components/MiPadron'
import {
    cerrarSesion,
    existeSesion
} from './services/api'


interface PadronElectoral {
    id: number
    cui: string
    nombre: string
    apellido: string
    correo: string | null

    departamentoId: number | null
    departamento: string

    municipioId: number | null
    municipio: string

    centroVotacionId: number | null
    centroVotacion: string | null

    mesaVotacionId: number | null
    mesaVotacion: string | null

    numeroLista: number | null
    hoja: number | null
    linea: number | null
    estado: string
}

export default function App() {
    const [registros, setRegistros] = useState<PadronElectoral[]>([])
    const [loading, setLoading] = useState(false)
    const [registroEditando, setRegistroEditando] = useState<PadronElectoral | null>(null)
    const [loginAbierto, setLoginAbierto] =
        useState(false)

    const [sesionIniciada, setSesionIniciada] =
        useState(existeSesion())
    const cargarRegistros = async () => {
        setLoading(true)

        try {
            const res = await fetch('/api/padron-electoral')

            if (!res.ok) {
                throw new Error('No fue posible cargar los registros')
            }

            const data = await res.json()
            setRegistros(data)
        } catch (error) {
            console.error('Error al cargar registros:', error)
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => {
        const manejarSesionExpirada = () => {
            cerrarSesion()
            setSesionIniciada(false)
            setLoginAbierto(true)
        }

        window.addEventListener(
            'sesion-expirada',
            manejarSesionExpirada
        )

        return () => {
            window.removeEventListener(
                'sesion-expirada',
                manejarSesionExpirada
            )
        }
    }, [])

    if (sesionIniciada) {
        return (
            <MiPadron
                onCerrarSesion={() => {
                    cerrarSesion()
                    setSesionIniciada(false)
                }}
            />
        )
    }

    return (
        <div className="min-h-screen bg-gray-50">
            <div className="max-w-6xl mx-auto px-4 py-8">
                <div className="mb-8 flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
                    <div>
                        <h1 className="text-4xl font-bold text-gray-900">
                            Padrón Electoral
                        </h1>

                        <p className="mt-2 text-gray-600">
                            Gestiona tu registro y consulta tu lugar
                            de votación.
                        </p>
                    </div>

                    <button
                        type="button"
                        onClick={() => {
                            console.log('Abriendo formulario de sesión')
                            setLoginAbierto(true)
                        }}
                        className="inline-flex items-center justify-center gap-2 rounded-lg bg-gray-900 px-5 py-3 font-medium text-white transition hover:bg-gray-800"
                    >
                        <LogIn size={18} />
                        Iniciar sesión
                    </button>
                </div>
                <p className="text-gray-600 mb-8">Gestiona los registros del padrón electoral</p>

                <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                    <div className="lg:col-span-1">
                        <FormularioPadron
                            onRegistroCreado={cargarRegistros}
                            registroEditando={registroEditando}
                        />
                    </div>

                    <div className="lg:col-span-2">
                        <TablaPadron
                            registros={registros}
                            loading={loading}
                            onRegistroEliminado={cargarRegistros}
                            onEditar={setRegistroEditando}
                        />

                    </div>
                </div>
            </div>
            <LoginModal
                abierto={loginAbierto}
                onCerrar={() =>
                    setLoginAbierto(false)
                }
                onLoginCorrecto={() => {
                    setLoginAbierto(false)
                    setSesionIniciada(true)
                }}
            />
        </div>
    )
}