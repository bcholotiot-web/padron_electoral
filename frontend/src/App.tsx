import { useState, useEffect } from 'react'
import FormularioPadron from './components/FormularioPadron'
import TablaPadron from './components/TablaPadron'

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
        cargarRegistros()
    }, [])

    return (
        <div className="min-h-screen bg-gray-50">
            <div className="max-w-6xl mx-auto px-4 py-8">
                <h1 className="text-4xl font-bold text-gray-900 mb-2">Padrón Electoral</h1>
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
        </div>
    )
}