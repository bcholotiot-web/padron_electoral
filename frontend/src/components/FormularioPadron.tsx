import { useState, useEffect } from 'react'
import { Plus } from 'lucide-react'
interface PadronElectoral {
    id: number
    cui: string
    nombre: string
    apellido: string
    correo: string

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

interface FormularioPadronProps {
    onRegistroCreado: () => void
    registroEditando: PadronElectoral | null
}

export default function FormularioPadron({ onRegistroCreado, registroEditando }: FormularioPadronProps) {
    const [departamentos, setDepartamentos] = useState<any[]>([])
    const [municipios, setMunicipios] = useState<any[]>([])
    const [centros, setCentros] = useState<any[]>([])
    const [mesas, setMesas] = useState<any[]>([])
    const [deptLoading, setDeptLoading] = useState(true)
    const [formData, setFormData] = useState({
        cui: '',
        nombre: '',
        apellido: '',
        correo: '',
        departamento: '',
        municipio: '',
        centroVotacion: '',
        mesaVotacion: ''
    })
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')
    const [success, setSuccess] = useState('')

    // Cargar departamentos una sola vez al montar el componente
    useEffect(() => {
        const cargarDepartamentos = async () => {
            try {
                const res = await fetch('/api/departamentos/activos')
                const data = await res.json()
                setDepartamentos(data)
            } catch (err) {
                console.error('Error al cargar departamentos:', err)
            } finally {
                setDeptLoading(false)
            }
        }
        cargarDepartamentos()
    },
        [])

    const cargarMunicipios = async (departamentoId: string) => {

        const res = await fetch(
            `/api/municipios/departamento/${departamentoId}`
        )

        const data = await res.json()

        setMunicipios(data)

        setCentros([])
        setMesas([])
    }

    const cargarCentros = async (municipioId: string) => {

        const res = await fetch(
            `/api/centros/municipio/${municipioId}`
        )

        const data = await res.json()

        setCentros(data)

        setMesas([])
    }

    const cargarMesas = async (centroId: string) => {

        const res = await fetch(
            `/api/mesas/centro/${centroId}`
        )

        const data = await res.json()

        setMesas(data)
    }

    const handleChange = async (
        e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
    ) => {

        const { name, value } = e.target

        console.log(name, value)

        setFormData(prev => ({
            ...prev,
            [name]: value

        }))

        if (name === "departamento" && value) {
            await cargarMunicipios(value)
        }

        if (name === "municipio" && value) {
            await cargarCentros(value)
        }

        if (name === "centroVotacion" && value) {
            await cargarMesas(value)
        }
    }
    const handleSubmit = async (
        e: React.FormEvent<HTMLFormElement>
    ) => {
        e.preventDefault()

        setError('')
        setSuccess('')
        setLoading(true)

        const datosParaGuardar = {
            cui: formData.cui.trim(),
            nombre: formData.nombre.trim(),
            apellido: formData.apellido.trim(),
            correo: formData.correo.trim(),
            departamentoId: Number(formData.departamento),
            municipioId: Number(formData.municipio),
            centroVotacionId: Number(formData.centroVotacion),
            mesaVotacionId: Number(formData.mesaVotacion)
        }

        try {
            const res = await fetch('/api/padron-electoral', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(datosParaGuardar)
            })

            if (!res.ok) {
                const contenido = await res.text()
                let mensaje = 'Error al crear el registro'

                if (contenido) {
                    try {
                        const respuestaError = JSON.parse(contenido)
                        mensaje =
                            respuestaError.mensaje ||
                            respuestaError.message ||
                            mensaje
                    } catch {
                        mensaje = contenido
                    }
                }

                throw new Error(mensaje)
            }

            const registroGuardado = await res.json()

            setSuccess(
                `Registro guardado. Lista ${registroGuardado.numeroLista}, ` +
                `hoja ${registroGuardado.hoja}, línea ${registroGuardado.linea}.`
            )

            setFormData({
                cui: '',
                nombre: '',
                apellido: '',
                correo: '',
                departamento: '',
                municipio: '',
                centroVotacion: '',
                mesaVotacion: ''
            })

            setMunicipios([])
            setCentros([])
            setMesas([])

            onRegistroCreado()
        } catch (err) {
            setError(
                err instanceof Error
                    ? err.message
                    : 'Ocurrió un error desconocido'
            )
        } finally {
            setLoading(false)
        }
    }

    return (
        <form onSubmit={handleSubmit} className="bg-white rounded-lg shadow p-6 animate-fade-in">
            <h2 className="text-xl font-bold text-gray-900 mb-4">Nuevo Registro</h2>

            <div className="space-y-4">
                <input
                    type="text"
                    name="cui"
                    placeholder="CUI"
                    value={formData.cui}
                    onChange={handleChange}
                    required
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-900"
                />
                <input
                    type="text"
                    name="nombre"
                    placeholder="Nombre"
                    value={formData.nombre}
                    onChange={handleChange}
                    required
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-900"
                />
                <input
                    type="text"
                    name="apellido"
                    placeholder="Apellido"
                    value={formData.apellido}
                    onChange={handleChange}
                    required
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-900"
                />
                <input
                    type="email"
                    name="correo"
                    placeholder="Correo electrónico"
                    value={formData.correo}
                    onChange={handleChange}
                    required
                    autoComplete="email"
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-900"
                />

                <select
                    name="departamento"
                    value={formData.departamento}
                    onChange={handleChange}
                    required
                    disabled={deptLoading}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-900 disabled:opacity-50"
                >
                    <option value="">
                        {deptLoading ? 'Cargando departamentos...' : 'Selecciona departamento'}
                    </option>
                    {departamentos.map((dept) => (
                        <option
                            key={dept.id}
                            value={dept.id}
                        >
                            {dept.nombre}
                        </option>
                    ))}
                </select>

                <select
                    name="municipio"
                    value={formData.municipio}
                    onChange={handleChange}
                    required
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg"
                >
                    <option value="">
                        Selecciona municipio
                    </option>

                    {municipios.map((m) => (
                        <option
                            key={m.id}
                            value={m.id}
                        >
                            {m.nombre}
                        </option>
                    ))}
                </select>

                <select
                    name="centroVotacion"
                    value={formData.centroVotacion}
                    onChange={handleChange}
                    required
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg"
                >
                    <option value="">
                        Selecciona centro
                    </option>

                    {centros.map((c) => (
                        <option
                            key={c.id}
                            value={c.id}
                        >
                            {c.nombre}
                        </option>
                    ))}
                </select>

                <select
                    name="mesaVotacion"
                    value={formData.mesaVotacion}
                    onChange={handleChange}
                    required
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg"
                >
                    <option value="">
                        Selecciona mesa
                    </option>

                    {mesas.map((m) => (
                        <option
                            key={m.id}
                            value={m.id}
                        >
                            {m.numeroMesa}
                        </option>
                    ))}
                </select>
            </div>

            {error && <div className="mt-4 p-3 bg-red-100 text-red-700 rounded-lg text-sm">{error}</div>}
            {success && <div className="mt-4 p-3 bg-green-100 text-green-700 rounded-lg text-sm">{success}</div>}

            <button
                type="submit"
                disabled={loading || deptLoading}
                className="w-full mt-4 bg-gray-900 text-white font-medium py-2 rounded-lg hover:bg-gray-800 transition disabled:opacity-50 flex items-center justify-center gap-2"
            >
                <Plus size={18} />
                {loading ? 'Creando...' : 'Guardar Registro'}
            </button>
        </form>

    )

}
